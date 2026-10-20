package com.gentlelady.reborn.server.memorial

import com.gentlelady.reborn.server.plugins.Db
import com.gentlelady.reborn.server.plugins.getStringList
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

    /** viewerId 가 볼 수 있는 추모 페이지만 ([MEMORIAL_VISIBLE]). */
    fun get(viewerId: UUID, id: UUID): MemorialResponse? =
        Db.queryFirst(
            """
            select m.id, m.creator_id, m.name, m.handle, m.bio, m.visibility, m.profile_image_url, m.follower_count
            from memorials m where m.id = ?
            """.trimIndent() + MEMORIAL_VISIBLE,
            id, viewerId, viewerId, viewerId, viewerId,
            map = ResultSet::toMemorial,
        )

    /** 존재하고 viewerId 가 볼 수 있는가. 못 보면 존재 여부도 숨긴다(404). */
    fun canView(viewerId: UUID, id: UUID): Boolean =
        Db.queryFirst(
            "select 1 from memorials m where m.id = ?" + MEMORIAL_VISIBLE,
            id, viewerId, viewerId, viewerId, viewerId,
        ) { true } ?: false

    fun follow(followerId: UUID, memorialId: UUID): Int =
        Db.update(
            "insert into memorial_follows (follower_id, memorial_id) values (?, ?) on conflict do nothing",
            followerId, memorialId,
        )

    fun unfollow(followerId: UUID, memorialId: UUID): Int =
        Db.update("delete from memorial_follows where follower_id = ? and memorial_id = ?", followerId, memorialId)

    fun createHistory(memorialId: UUID, authorId: UUID, req: CreateHistoryRequest): HistoryResponse {
        val id = Db.queryFirst(
            "insert into memorial_history (memorial_id, author_id, caption, image_urls) values (?, ?, ?, ?) returning id",
            memorialId, authorId, req.caption, req.imageUrls.toTypedArray(),
        ) { it.getString("id") } ?: error("history insert returned nothing")
        return getHistory(authorId, UUID.fromString(id)) ?: error("history $id vanished after insert")
    }

    /** 히스토리 한 건. 소속 추모 페이지를 볼 수 없으면 null. */
    fun getHistory(viewerId: UUID, id: UUID): HistoryResponse? =
        Db.queryFirst(
            HISTORY_SELECT + " join memorials m on m.id = h.memorial_id where h.id = ?" + MEMORIAL_VISIBLE,
            viewerId, id, viewerId, viewerId, viewerId, viewerId,
            map = ResultSet::toHistory,
        )

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

    /**
     * 최신순. [before] 를 주면 그 (작성 시각, id) 보다 이전 것만 — 스크롤로 이전 글을 이어 읽을 때.
     * offset 과 달리 그 사이 새 글이 생겨도 겹치거나 건너뛰지 않고, 기준 글이 지워져도 이어진다.
     */
    fun listGuestbook(memorialId: UUID, before: GuestbookCursor?, offset: Int, limit: Int): List<GuestbookEntryResponse> =
        Db.query(
            GUESTBOOK_SELECT + """
            where g.memorial_id = ?
              and (?::timestamptz is null or (g.created_at, g.id) < (?::timestamptz, ?::uuid))
            order by g.created_at desc, g.id desc
            limit ? offset ?
            """.trimIndent(),
            memorialId, before?.createdAt, before?.createdAt, before?.id, limit, offset,
            map = ResultSet::toGuestbookEntry,
        )

    /** [after] 보다 최신 것 [limit] 개를 최신순으로 — 아래로 스크롤해 이후 글을 이어 읽을 때. */
    fun listGuestbookNewer(memorialId: UUID, after: GuestbookCursor, limit: Int): List<GuestbookEntryResponse> =
        Db.query(
            GUESTBOOK_SELECT + """
            where g.memorial_id = ? and (g.created_at, g.id) > (?::timestamptz, ?::uuid)
            order by g.created_at asc, g.id asc
            limit ?
            """.trimIndent(),
            memorialId, after.createdAt, after.id, limit,
            map = ResultSet::toGuestbookEntry,
        ).reversed()

    fun getGuestbookEntry(memorialId: UUID, entryId: UUID): GuestbookEntryResponse? =
        Db.queryFirst(
            GUESTBOOK_SELECT + " where g.memorial_id = ? and g.id = ?",
            memorialId, entryId,
            map = ResultSet::toGuestbookEntry,
        )

    /** 내가 남긴 방명록 (최신순, 커서). 지금 볼 수 없는 추모 페이지(비공개 전환·차단)의 글은 뺀다. */
    fun listMyGuestbook(viewerId: UUID, before: GuestbookCursor?, limit: Int): List<MyGuestbookEntryResponse> =
        Db.query(
            """
            select g.id, g.memorial_id, m.name as memorial_name, m.profile_image_url as memorial_profile_image_url,
                   g.message, g.created_at
            from guestbook_entries g
            join memorials m on m.id = g.memorial_id
            where g.author_id = ?
            """.trimIndent() + MEMORIAL_VISIBLE + """
              and (?::timestamptz is null or (g.created_at, g.id) < (?::timestamptz, ?::uuid))
            order by g.created_at desc, g.id desc
            limit ?
            """.trimIndent(),
            viewerId, viewerId, viewerId, viewerId, viewerId,
            before?.createdAt, before?.createdAt, before?.id, limit,
        ) {
            MyGuestbookEntryResponse(
                id = it.getString("id"),
                memorialId = it.getString("memorial_id"),
                memorialName = it.getString("memorial_name"),
                memorialProfileImageUrl = it.getString("memorial_profile_image_url"),
                message = it.getString("message"),
                createdAt = it.getObject("created_at", OffsetDateTime::class.java).toString(),
            )
        }

    /** 본인 방명록만 삭제. 삭제된 행 수(0 = 없음 또는 남의 글). */
    fun deleteGuestbookEntry(entryId: UUID, requesterId: UUID): Int =
        Db.update("delete from guestbook_entries where id = ? and author_id = ?", entryId, requesterId)
}

/** 방명록 + 작성자 프로필. where 절은 호출부가 붙인다. */
private const val GUESTBOOK_SELECT = """
    select g.id, g.memorial_id, g.author_id, pr.display_name as author_name,
           pr.avatar_url as author_avatar_url, g.message, g.created_at
    from guestbook_entries g
    join profiles pr on pr.id = g.author_id
"""

/**
 * 추모 페이지 공개 범위 (별칭 `m`). `?` 4개 = 전부 viewerId.
 * public = 누구나 · followers = 개설자를 팔로우하는 사람 · private = 개설자만. 개설자와 양방향 차단이면 안 보인다.
 */
private const val MEMORIAL_VISIBLE = """
    and (
        m.creator_id = ?
        or m.visibility = 'public'
        or (
            m.visibility = 'followers'
            and exists(select 1 from follows f where f.follower_id = ? and f.followee_id = m.creator_id)
        )
    )
    and not exists (
        select 1 from blocks b
        where (b.blocker_id = ? and b.blocked_id = m.creator_id)
           or (b.blocker_id = m.creator_id and b.blocked_id = ?)
    )
"""

/** 첫 `?` = viewerId (liked 서브쿼리). where 절 파라미터는 그 뒤에 온다. */
private const val HISTORY_SELECT = """
    select h.id, h.memorial_id, h.author_id, pr.display_name as author_name, pr.avatar_url as author_avatar_url,
           h.caption, h.image_urls, h.like_count, h.comment_count, h.created_at,
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
    imageUrls = getStringList("image_urls"),
    likeCount = getInt("like_count"),
    commentCount = getInt("comment_count"),
    liked = getBoolean("liked"),
    createdAt = getObject("created_at", OffsetDateTime::class.java).toString(),
)

/** 방명록 커서 = 마지막으로 받은 글의 (작성 시각, id). 문자열 형식 `{ISO-8601}_{uuid}`. */
data class GuestbookCursor(val createdAt: OffsetDateTime, val id: UUID) {
    override fun toString() = "${createdAt}_$id"

    companion object {
        /** 형식이 틀리면 IllegalArgumentException(→ 400). */
        fun parse(raw: String): GuestbookCursor {
            val at = raw.substringBeforeLast('_', missingDelimiterValue = "")
            val id = raw.substringAfterLast('_')
            val createdAt = runCatching { OffsetDateTime.parse(at) }
                .getOrElse { throw IllegalArgumentException("bad cursor") }
            return GuestbookCursor(createdAt, UUID.fromString(id))
        }

        fun after(entry: GuestbookEntryResponse) = of(entry.createdAt, entry.id)

        fun of(createdAt: String, id: String) = GuestbookCursor(OffsetDateTime.parse(createdAt), UUID.fromString(id))
    }
}
