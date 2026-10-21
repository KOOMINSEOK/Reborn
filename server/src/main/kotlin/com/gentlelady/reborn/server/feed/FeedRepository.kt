package com.gentlelady.reborn.server.feed

import com.gentlelady.reborn.server.plugins.Db
import com.gentlelady.reborn.server.plugins.getStringList
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.UUID

/**
 * 내 프로필 글(posts) + 홈 피드 + 사람↔사람 팔로우/차단.
 * ORM 없이 손으로 쓴 SQL — 추천 랭킹이 SQL 표현식이라 DSL 로 감싸면 오히려 읽기 어렵다.
 */
class FeedRepository {

    /**
     * 팔로우. 성공이면 null, 막히면 에러 코드.
     * - 없는 사람 / 나를 차단한 사람 → user_not_found (차단 사실을 알리지 않는다)
     * - 내가 차단한 사람 → blocked (차단 해제부터)
     * - 비공개 계정 → private_account. ponytail: 팔로우 요청(승인) 기능이 생기기 전까진 막는다
     *   (그냥 받아주면 비공개 글이 바로 보인다). 이미 팔로우 중이면 그대로 성공.
     */
    fun followUser(followerId: UUID, followeeId: UUID): String? {
        require(followerId != followeeId) { "cannot follow yourself" }
        val target = Db.queryFirst(
            """
            select p.is_private,
                   exists(select 1 from blocks where blocker_id = p.id and blocked_id = ?) as blocks_me,
                   exists(select 1 from blocks where blocker_id = ? and blocked_id = p.id) as blocked_by_me,
                   exists(select 1 from follows where follower_id = ? and followee_id = p.id) as following
            from profiles p where p.id = ?
            """.trimIndent(),
            followerId, followerId, followerId, followeeId,
        ) { Triple(it.getBoolean("is_private"), it.getBoolean("blocks_me") to it.getBoolean("blocked_by_me"), it.getBoolean("following")) }
            ?: return "user_not_found"
        val (isPrivate, blocks, following) = target
        return when {
            blocks.first -> "user_not_found"
            blocks.second -> "blocked"
            following -> null
            isPrivate -> "private_account"
            else -> {
                Db.update(
                    "insert into follows (follower_id, followee_id) values (?, ?) on conflict do nothing",
                    followerId, followeeId,
                )
                null
            }
        }
    }

    fun unfollowUser(followerId: UUID, followeeId: UUID): Int =
        Db.update("delete from follows where follower_id = ? and followee_id = ?", followerId, followeeId)

    fun blockUser(blockerId: UUID, blockedId: UUID): Int {
        require(blockerId != blockedId) { "cannot block yourself" }
        // 차단하면 양방향 팔로우도 끊는다.
        Db.update(
            "delete from follows where (follower_id = ? and followee_id = ?) or (follower_id = ? and followee_id = ?)",
            blockerId, blockedId, blockedId, blockerId,
        )
        return Db.update(
            "insert into blocks (blocker_id, blocked_id) values (?, ?) on conflict do nothing",
            blockerId, blockedId,
        )
    }

    fun unblockUser(blockerId: UUID, blockedId: UUID): Int =
        Db.update("delete from blocks where blocker_id = ? and blocked_id = ?", blockerId, blockedId)

    fun listBlocked(blockerId: UUID): List<BlockedUserResponse> =
        Db.query(
            """
            select pr.id, pr.display_name as name, pr.handle, pr.avatar_url
            from blocks b
            join profiles pr on pr.id = b.blocked_id
            where b.blocker_id = ?
            order by b.created_at desc
            """.trimIndent(),
            blockerId,
        ) {
            BlockedUserResponse(
                id = it.getString("id"),
                name = it.getString("name"),
                handle = it.getString("handle"),
                avatarUrl = it.getString("avatar_url"),
            )
        }

    fun createPost(authorId: UUID, req: CreatePostRequest): PostResponse {
        val publishAt = req.publishAt?.let {
            runCatching { OffsetDateTime.parse(it) }
                .getOrElse { throw IllegalArgumentException("publishAt must be ISO-8601") }
        }
        val status = if (publishAt != null) "scheduled" else "published"
        val requestId = req.clientRequestId?.let(UUID::fromString) // 형식 오류 → IllegalArgumentException → 400
        val id = Db.queryFirst(
            """
            insert into posts (author_id, caption, image_urls, is_posthumous, status, publish_at, client_request_id)
            values (?, ?, ?, ?, ?, ?, ?)
            on conflict (author_id, client_request_id) where client_request_id is not null do nothing
            returning id
            """.trimIndent(),
            authorId, req.caption, req.imageUrls.toTypedArray(), req.isPosthumous, status, publishAt, requestId,
        ) { it.getString("id") }
            // 충돌 = 같은 키로 이미 만든 글이 있음 (응답 유실 후 재시도) → 그 글을 돌려준다
            ?: requestId?.let { existingPostId(authorId, it) }
            ?: error("post insert returned nothing")

        return getPost(authorId, UUID.fromString(id)) ?: error("post $id vanished after insert")
    }

    private fun existingPostId(authorId: UUID, clientRequestId: UUID): String? =
        Db.queryFirst(
            "select id from posts where author_id = ? and client_request_id = ?",
            authorId, clientRequestId,
        ) { it.getString("id") }

    /**
     * viewerId 관점의 게시물 한 건 (liked 플래그 포함).
     * 작성자 본인이 아니면: 발행된 글만, 비공개 계정이면 팔로워만, 차단 관계면 안 보인다.
     */
    fun getPost(viewerId: UUID, id: UUID): PostResponse? =
        Db.queryFirst(
            POST_SELECT + " where p.id = ?" + POST_VISIBLE + NOT_BLOCKED,
            viewerId, id, viewerId, viewerId, viewerId, viewerId,
        ) {
            it.toPost("following")
        }

    /**
     * 한 사람의 발행된 글, 최신순 (프로필 화면). 볼 수 없는 사람(비공개·차단)이면 빈 목록 —
     * 예약 글은 본인에게도 안 보인다 (예약 글은 관리 화면에서).
     */
    fun userPosts(viewerId: UUID, authorId: UUID, offset: Int, limit: Int): List<PostResponse> =
        Db.query(
            POST_SELECT + " where p.author_id = ? and p.status = 'published'" + POST_VISIBLE + NOT_BLOCKED + """
            order by p.created_at desc, p.id desc
            offset ? limit ?
            """.trimIndent(),
            viewerId, authorId, viewerId, viewerId, viewerId, viewerId, offset, limit,
        ) { it.toPost("profile") }

    /** 내 글 + 팔로우한 사람들의 최신 게시물. (내 글이 빠지면 방금 올린 글이 홈에 안 보인다) */
    fun followingFeed(viewerId: UUID, limit: Int): List<PostResponse> =
        Db.query(
            POST_SELECT + """
            where p.status = 'published'
              and (p.author_id = ? or p.author_id in (select followee_id from follows where follower_id = ?))
            """.trimIndent() + NOT_BLOCKED + """
            order by p.created_at desc
            limit ?
            """.trimIndent(),
            viewerId, viewerId, viewerId, viewerId, viewerId, limit,
        ) { it.toPost("following") }

    /**
     * 추천: 안 팔로우 + 비공개 아님 + 미차단 + 남의 글. HackerNews 식 시간감쇠 랭킹.
     * ponytail: 결정적 score 정렬(무작위 없음) → offset 페이지네이션이 일관됨.
     */
    fun recommendedFeed(viewerId: UUID, limit: Int): List<PostResponse> =
        Db.query(
            POST_SELECT + """
            where p.status = 'published'
              and pr.is_private = false
              and p.author_id <> ?
              and p.author_id not in (select followee_id from follows where follower_id = ?)
            """.trimIndent() + NOT_BLOCKED + """
            order by (
                ln(1 + p.like_count + p.comment_count * 2)
                - extract(epoch from now() - p.created_at) / 45000.0
            ) desc
            limit ?
            """.trimIndent(),
            viewerId, viewerId, viewerId, viewerId, viewerId, limit,
        ) { it.toPost("recommended") }
}

/** 첫 `?` = viewerId (liked 서브쿼리). where 절 파라미터는 그 뒤에 온다. */
private const val POST_SELECT = """
    select p.id, p.author_id, pr.display_name as author_name, pr.handle as author_handle,
           pr.avatar_url as author_avatar_url,
           p.caption, p.image_urls, p.is_posthumous, p.status,
           p.like_count, p.comment_count, p.created_at,
           exists(select 1 from post_likes pl where pl.post_id = p.id and pl.user_id = ?) as liked
    from posts p
    join profiles pr on pr.id = p.author_id
"""

/** 본인 글이거나, 발행됐고 (공개 계정 또는 내가 팔로우 중). `?` 2개 = viewerId, viewerId. */
private const val POST_VISIBLE = """
    and (
        p.author_id = ?
        or (
            p.status = 'published'
            and (
                pr.is_private = false
                or exists(select 1 from follows f where f.follower_id = ? and f.followee_id = p.author_id)
            )
        )
    )
"""

/** 양방향 차단 제외. `?` 2개 = viewerId, viewerId. */
private const val NOT_BLOCKED = """
    and not exists (
        select 1 from blocks b
        where (b.blocker_id = ? and b.blocked_id = p.author_id)
           or (b.blocker_id = p.author_id and b.blocked_id = ?)
    )
"""

private fun ResultSet.toPost(source: String) = PostResponse(
    id = getString("id"),
    authorId = getString("author_id"),
    authorName = getString("author_name"),
    authorHandle = getString("author_handle"),
    authorAvatarUrl = getString("author_avatar_url"),
    caption = getString("caption"),
    imageUrls = getStringList("image_urls"),
    isPosthumous = getBoolean("is_posthumous"),
    status = getString("status"),
    likeCount = getInt("like_count"),
    commentCount = getInt("comment_count"),
    liked = getBoolean("liked"),
    createdAt = getObject("created_at", OffsetDateTime::class.java).toString(),
    source = source,
)
