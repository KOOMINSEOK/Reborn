package com.gentlelady.reborn.server.feed

import com.gentlelady.reborn.server.plugins.Db
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.UUID

/**
 * 내 프로필 글(posts) + 홈 피드 + 사람↔사람 팔로우/차단.
 * ORM 없이 손으로 쓴 SQL — 추천 랭킹이 SQL 표현식이라 DSL 로 감싸면 오히려 읽기 어렵다.
 */
class FeedRepository {

    fun followUser(followerId: UUID, followeeId: UUID): Int {
        require(followerId != followeeId) { "cannot follow yourself" }
        return Db.update(
            "insert into follows (follower_id, followee_id) values (?, ?) on conflict do nothing",
            followerId, followeeId,
        )
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
        val id = Db.queryFirst(
            """
            insert into posts (author_id, caption, image_url, is_posthumous, status, publish_at)
            values (?, ?, ?, ?, ?, ?)
            returning id
            """.trimIndent(),
            authorId, req.caption, req.imageUrl, req.isPosthumous, status, publishAt,
        ) { it.getString("id") } ?: error("post insert returned nothing")

        return getPost(authorId, UUID.fromString(id)) ?: error("post $id vanished after insert")
    }

    /** viewerId 관점의 게시물 한 건 (liked 플래그 포함). 차단 관계면 안 보인다. */
    fun getPost(viewerId: UUID, id: UUID): PostResponse? =
        Db.queryFirst(POST_SELECT + " where p.id = ?" + NOT_BLOCKED, viewerId, id, viewerId, viewerId) {
            it.toPost("following")
        }

    /** 팔로우한 사람들의 최신 게시물. */
    fun followingFeed(viewerId: UUID, limit: Int): List<PostResponse> =
        Db.query(
            POST_SELECT + """
            where p.status = 'published'
              and p.author_id in (select followee_id from follows where follower_id = ?)
            """.trimIndent() + NOT_BLOCKED + """
            order by p.created_at desc
            limit ?
            """.trimIndent(),
            viewerId, viewerId, viewerId, viewerId, limit,
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
           p.caption, p.image_url, p.is_posthumous, p.status,
           p.like_count, p.comment_count, p.created_at,
           exists(select 1 from post_likes pl where pl.post_id = p.id and pl.user_id = ?) as liked
    from posts p
    join profiles pr on pr.id = p.author_id
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
    imageUrl = getString("image_url"),
    isPosthumous = getBoolean("is_posthumous"),
    status = getString("status"),
    likeCount = getInt("like_count"),
    commentCount = getInt("comment_count"),
    liked = getBoolean("liked"),
    createdAt = getObject("created_at", OffsetDateTime::class.java).toString(),
    source = source,
)
