package com.gentlelady.reborn.server.memorial

import com.gentlelady.reborn.server.plugins.Db
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.UUID

/** 추모 페이지(memorials) + 히스토리(추억) 글. 팔로우 수는 DB 트리거가 관리. */
class MemorialRepository {

    fun create(creatorId: UUID, req: CreateMemorialRequest): MemorialResponse =
        Db.queryFirst(
            """
            insert into memorials (creator_id, name, handle, bio, visibility, profile_image_url)
            values (?, ?, ?, ?, ?, ?)
            returning id, creator_id, name, handle, bio, visibility, profile_image_url, follower_count
            """.trimIndent(),
            creatorId, req.name, req.handle, req.bio, req.visibility, req.profileImageUrl,
            map = ResultSet::toMemorial,
        ) ?: error("memorial insert returned nothing")

    fun get(id: UUID): MemorialResponse? =
        Db.queryFirst(
            """
            select id, creator_id, name, handle, bio, visibility, profile_image_url, follower_count
            from memorials where id = ?
            """.trimIndent(),
            id,
            map = ResultSet::toMemorial,
        )

    fun exists(id: UUID): Boolean =
        Db.queryFirst("select 1 from memorials where id = ?", id) { true } ?: false

    fun follow(followerId: UUID, memorialId: UUID): Int =
        Db.update(
            "insert into memorial_follows (follower_id, memorial_id) values (?, ?) on conflict do nothing",
            followerId, memorialId,
        )

    fun unfollow(followerId: UUID, memorialId: UUID): Int =
        Db.update("delete from memorial_follows where follower_id = ? and memorial_id = ?", followerId, memorialId)

    fun createHistory(memorialId: UUID, authorId: UUID, req: CreateHistoryRequest): HistoryResponse {
        val id = Db.queryFirst(
            "insert into memorial_history (memorial_id, author_id, caption, image_url) values (?, ?, ?, ?) returning id",
            memorialId, authorId, req.caption, req.imageUrl,
        ) { it.getString("id") } ?: error("history insert returned nothing")
        return getHistory(authorId, UUID.fromString(id)) ?: error("history $id vanished after insert")
    }

    fun getHistory(viewerId: UUID, id: UUID): HistoryResponse? =
        Db.queryFirst(HISTORY_SELECT + " where h.id = ?", viewerId, id, map = ResultSet::toHistory)

    fun listHistory(viewerId: UUID, memorialId: UUID, offset: Int, limit: Int): List<HistoryResponse> =
        Db.query(
            HISTORY_SELECT + " where h.memorial_id = ? order by h.created_at desc limit ? offset ?",
            viewerId, memorialId, limit, offset,
            map = ResultSet::toHistory,
        )

    fun createGuestbookEntry(memorialId: UUID, authorId: UUID, message: String): GuestbookEntryResponse =
        Db.queryFirst(
            """
            with g as (
                insert into guestbook_entries (memorial_id, author_id, message)
                values (?, ?, ?)
                returning id, memorial_id, author_id, message, created_at
            )
            select g.id, g.memorial_id, g.author_id, pr.display_name as author_name,
                   pr.avatar_url as author_avatar_url, g.message, g.created_at
            from g join profiles pr on pr.id = g.author_id
            """.trimIndent(),
            memorialId, authorId, message,
            map = ResultSet::toGuestbookEntry,
        ) ?: error("guestbook insert returned nothing")

    fun listGuestbook(memorialId: UUID, offset: Int, limit: Int): List<GuestbookEntryResponse> =
        Db.query(
            """
            select g.id, g.memorial_id, g.author_id, pr.display_name as author_name,
                   pr.avatar_url as author_avatar_url, g.message, g.created_at
            from guestbook_entries g
            join profiles pr on pr.id = g.author_id
            where g.memorial_id = ?
            order by g.created_at desc
            limit ? offset ?
            """.trimIndent(),
            memorialId, limit, offset,
            map = ResultSet::toGuestbookEntry,
        )

    /** 본인 방명록만 삭제. 삭제된 행 수(0 = 없음 또는 남의 글). */
    fun deleteGuestbookEntry(entryId: UUID, requesterId: UUID): Int =
        Db.update("delete from guestbook_entries where id = ? and author_id = ?", entryId, requesterId)
}

/** 첫 `?` = viewerId (liked 서브쿼리). where 절 파라미터는 그 뒤에 온다. */
private const val HISTORY_SELECT = """
    select h.id, h.memorial_id, h.author_id, pr.display_name as author_name, pr.avatar_url as author_avatar_url,
           h.caption, h.image_url, h.like_count, h.comment_count, h.created_at,
           exists(select 1 from history_likes hl where hl.history_id = h.id and hl.user_id = ?) as liked
    from memorial_history h
    join profiles pr on pr.id = h.author_id
"""

private fun ResultSet.toMemorial() = MemorialResponse(
    id = getString("id"),
    creatorId = getString("creator_id"),
    name = getString("name"),
    handle = getString("handle"),
    bio = getString("bio"),
    visibility = getString("visibility"),
    profileImageUrl = getString("profile_image_url"),
    followerCount = getInt("follower_count"),
)

private fun ResultSet.toGuestbookEntry() = GuestbookEntryResponse(
    id = getString("id"),
    memorialId = getString("memorial_id"),
    authorId = getString("author_id"),
    authorName = getString("author_name"),
    authorAvatarUrl = getString("author_avatar_url"),
    message = getString("message"),
    createdAt = getObject("created_at", OffsetDateTime::class.java).toString(),
)

private fun ResultSet.toHistory() = HistoryResponse(
    id = getString("id"),
    memorialId = getString("memorial_id"),
    authorId = getString("author_id"),
    authorName = getString("author_name"),
    authorAvatarUrl = getString("author_avatar_url"),
    caption = getString("caption"),
    imageUrl = getString("image_url"),
    likeCount = getInt("like_count"),
    commentCount = getInt("comment_count"),
    liked = getBoolean("liked"),
    createdAt = getObject("created_at", OffsetDateTime::class.java).toString(),
)
