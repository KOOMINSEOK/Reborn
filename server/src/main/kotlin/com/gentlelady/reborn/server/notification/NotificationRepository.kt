package com.gentlelady.reborn.server.notification

import com.gentlelady.reborn.server.memorial.GuestbookCursor
import com.gentlelady.reborn.server.plugins.Db
import kotlinx.serialization.Serializable
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.UUID

@Serializable
data class NotificationResponse(
    val id: String,
    /** post_like · post_comment · history_like · history_comment · follow · guestbook */
    val type: String,
    val actorId: String,
    val actorName: String,
    val actorHandle: String,
    val actorAvatarUrl: String? = null,
    val postId: String? = null,
    val historyId: String? = null,
    val memorialId: String? = null,
    val memorialName: String? = null,
    /** 댓글·방명록 id */
    val sourceId: String? = null,
    /** 댓글·방명록 앞부분 */
    val preview: String? = null,
    /** 그 글의 첫 사진 (목록 오른쪽 작은 사진) */
    val imageUrl: String? = null,
    val createdAt: String,
    val read: Boolean,
)

@Serializable
data class NotificationListResponse(val items: List<NotificationResponse>, val nextCursor: String? = null)

@Serializable
data class UnreadCountResponse(val count: Int)

/**
 * 알림은 DB 트리거가 만든다 (V12). 여기선 읽기·읽음 처리만.
 * 나와 차단 관계(어느 쪽이든)인 사람의 알림은 보이지 않는다 — 차단을 풀면 다시 보인다.
 */
class NotificationRepository {

    /** 최신순. [before] = 지난 페이지 마지막 알림의 (시각, id). */
    fun list(recipientId: UUID, before: GuestbookCursor?, limit: Int): List<NotificationResponse> =
        Db.query(
            """
            select n.id, n.type, n.actor_id, a.display_name as actor_name, a.handle as actor_handle,
                   a.avatar_url as actor_avatar_url, n.post_id, n.history_id, n.memorial_id, m.name as memorial_name,
                   n.source_id, n.preview, coalesce(p.image_urls[1], h.image_urls[1]) as image_url,
                   n.created_at, n.read_at is not null as read
            from notifications n
            join profiles a on a.id = n.actor_id
            left join posts p on p.id = n.post_id
            left join memorial_history h on h.id = n.history_id
            left join memorials m on m.id = n.memorial_id
            where n.recipient_id = ?
            """.trimIndent() + NOT_BLOCKED + """
              and (?::timestamptz is null or (n.created_at, n.id) < (?::timestamptz, ?::uuid))
            order by n.created_at desc, n.id desc
            limit ?
            """.trimIndent(),
            recipientId, recipientId, recipientId,
            before?.createdAt, before?.createdAt, before?.id, limit,
            map = ResultSet::toNotification,
        )

    fun unreadCount(recipientId: UUID): Int =
        Db.queryFirst(
            "select count(*) as c from notifications n where n.recipient_id = ? and n.read_at is null" + NOT_BLOCKED,
            recipientId, recipientId, recipientId,
        ) { it.getInt("c") } ?: 0

    /**
     * [upTo] 까지의 알림을 읽음으로. 앱은 자기가 받은 가장 최신 알림의 시각(서버가 준 값)을 보낸다 —
     * 목록을 보는 사이 새로 온 알림까지 읽음 처리하지 않게. null 이면 DB 시각 기준 지금까지.
     * (API 서버·폰 시계는 DB 와 몇 초씩 다를 수 있어 그 시계로 "지금"을 정하지 않는다)
     */
    fun markRead(recipientId: UUID, upTo: OffsetDateTime?): Int =
        Db.update(
            """
            update notifications set read_at = now()
            where recipient_id = ? and read_at is null and created_at <= coalesce(?::timestamptz, now())
            """.trimIndent(),
            recipientId, upTo,
        )
}

/** 알림을 만든 사람과 받는 사람이 차단 관계면 뺀다. `?` 2개 = recipientId, recipientId. */
private const val NOT_BLOCKED = """
    and not exists (
        select 1 from blocks b
        where (b.blocker_id = ? and b.blocked_id = n.actor_id)
           or (b.blocker_id = n.actor_id and b.blocked_id = ?)
    )
"""

private fun ResultSet.toNotification() = NotificationResponse(
    id = getString("id"),
    type = getString("type"),
    actorId = getString("actor_id"),
    actorName = getString("actor_name"),
    actorHandle = getString("actor_handle"),
    actorAvatarUrl = getString("actor_avatar_url"),
    postId = getString("post_id"),
    historyId = getString("history_id"),
    memorialId = getString("memorial_id"),
    memorialName = getString("memorial_name"),
    sourceId = getString("source_id"),
    preview = getString("preview"),
    imageUrl = getString("image_url"),
    createdAt = getObject("created_at", OffsetDateTime::class.java).toString(),
    read = getBoolean("read"),
)
