package com.gentlelady.reborn.server.feed

import com.gentlelady.reborn.server.plugins.Db
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.UUID

/**
 * 피드/소셜그래프 관련 모든 SQL. ORM 없이 손으로 쓴 쿼리 — 추천 랭킹이 SQL 표현식이라
 * DSL 로 감싸면 오히려 읽기 어려워진다.
 */
class FeedRepository {

    fun createMemorial(ownerId: UUID, req: CreateMemorialRequest): MemorialResponse =
        Db.queryFirst(
            """
            insert into memorials (owner_id, name, handle, bio, is_posthumous, visibility, profile_image_url)
            values (?, ?, ?, ?, ?, ?, ?)
            returning id, owner_id, name, handle, bio, is_posthumous, visibility, profile_image_url, follower_count
            """.trimIndent(),
            ownerId, req.name, req.handle, req.bio, req.isPosthumous, req.visibility, req.profileImageUrl,
            map = ResultSet::toMemorial,
        ) ?: error("memorial insert returned nothing")

    fun getMemorial(id: UUID): MemorialResponse? =
        Db.queryFirst(
            """
            select id, owner_id, name, handle, bio, is_posthumous, visibility, profile_image_url, follower_count
            from memorials where id = ?
            """.trimIndent(),
            id,
            map = ResultSet::toMemorial,
        )

    fun memorialExists(id: UUID): Boolean =
        Db.queryFirst("select 1 from memorials where id = ?", id) { true } ?: false

    fun follow(followerId: UUID, memorialId: UUID): Int =
        Db.update(
            "insert into follows (follower_id, memorial_id) values (?, ?) on conflict do nothing",
            followerId, memorialId,
        )

    fun unfollow(followerId: UUID, memorialId: UUID): Int =
        Db.update("delete from follows where follower_id = ? and memorial_id = ?", followerId, memorialId)

    fun createPost(authorId: UUID, req: CreatePostRequest): PostResponse {
        val publishAt = req.publishAt?.let {
            runCatching { OffsetDateTime.parse(it) }
                .getOrElse { throw IllegalArgumentException("publishAt must be ISO-8601") }
        }
        val status = if (publishAt != null) "scheduled" else "published"
        val id = Db.queryFirst(
            """
            insert into posts (memorial_id, author_id, caption, image_url, is_posthumous, status, publish_at)
            values (?, ?, ?, ?, ?, ?, ?)
            returning id
            """.trimIndent(),
            UUID.fromString(req.memorialId), authorId, req.caption, req.imageUrl,
            req.isPosthumous, status, publishAt,
        ) { it.getString("id") } ?: error("post insert returned nothing")

        return getPost(authorId, UUID.fromString(id)) ?: error("post $id vanished after insert")
    }

    /** viewerId 관점의 게시물 한 건 (liked 플래그 포함). */
    fun getPost(viewerId: UUID, id: UUID): PostResponse? =
        Db.queryFirst(POST_SELECT + " where p.id = ?", viewerId, id) { it.toPost("following") }

    /** 팔로우한 추모의 최신 게시물. */
    fun followingFeed(viewerId: UUID, limit: Int): List<PostResponse> =
        Db.query(
            POST_SELECT + """
            where p.status = 'published'
              and p.memorial_id in (select memorial_id from follows where follower_id = ?)
            order by p.created_at desc
            limit ?
            """.trimIndent(),
            viewerId, viewerId, limit,
        ) { it.toPost("following") }

    /**
     * 추천 게시물: 안 팔로우 + 공개 추모 + 남의 글. HackerNews 식 시간감쇠 랭킹.
     * ponytail: 결정적 score 정렬(무작위 없음) → offset 페이지네이션이 일관됨.
     * 다양성용 무작위는 시드 기반으로 나중에. 차단 필터는 blocks 테이블 생기면 추가.
     */
    fun recommendedFeed(viewerId: UUID, limit: Int): List<PostResponse> =
        Db.query(
            POST_SELECT + """
            where p.status = 'published'
              and m.visibility = 'public'
              and p.author_id <> ?
              and p.memorial_id not in (select memorial_id from follows where follower_id = ?)
            order by (
                ln(1 + p.like_count + p.comment_count * 2)
                - extract(epoch from now() - p.created_at) / 45000.0
            ) desc
            limit ?
            """.trimIndent(),
            viewerId, viewerId, viewerId, limit,
        ) { it.toPost("recommended") }
}

/** 첫 `?` = viewerId (liked 서브쿼리). where 절 파라미터는 그 뒤에 온다. */
private const val POST_SELECT = """
    select p.id, p.memorial_id, m.name as memorial_name, m.handle as memorial_handle,
           p.author_id, p.caption, p.image_url, p.is_posthumous, p.status,
           p.like_count, p.comment_count, p.created_at,
           exists(select 1 from post_likes pl where pl.post_id = p.id and pl.user_id = ?) as liked
    from posts p
    join memorials m on m.id = p.memorial_id
"""

private fun ResultSet.toMemorial() = MemorialResponse(
    id = getString("id"),
    ownerId = getString("owner_id"),
    name = getString("name"),
    handle = getString("handle"),
    bio = getString("bio"),
    isPosthumous = getBoolean("is_posthumous"),
    visibility = getString("visibility"),
    profileImageUrl = getString("profile_image_url"),
    followerCount = getInt("follower_count"),
)

private fun ResultSet.toPost(source: String) = PostResponse(
    id = getString("id"),
    memorialId = getString("memorial_id"),
    memorialName = getString("memorial_name"),
    memorialHandle = getString("memorial_handle"),
    authorId = getString("author_id"),
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
