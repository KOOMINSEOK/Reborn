package com.gentlelady.reborn.server.notification

import com.gentlelady.reborn.server.memorial.GuestbookCursor
import com.gentlelady.reborn.server.memorial.MEMORIAL_VISIBLE
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
 * 보이는 알림 = [VISIBLE]: 나와 차단 관계(어느 쪽이든)인 사람의 알림, 지금 내가 볼 수 없는 추모 페이지
 * (비공개 전환·개설자와 차단)의 알림은 빼고 — 상황이 바뀌면 다시 보인다.
 * 목록·안 읽은 수·읽음 처리가 모두 같은 조건을 쓴다 (안 보이는 알림을 몰래 읽음 처리하지 않게).
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
            """.trimIndent() + VISIBLE + """
              and (?::timestamptz is null or (n.created_at, n.id) < (?::timestamptz, ?::uuid))
            order by n.created_at desc, n.id desc
            limit ?
            """.trimIndent(),
            recipientId, *visibleParams(recipientId),
            before?.createdAt, before?.createdAt, before?.id, limit,
            map = ResultSet::toNotification,
        )

    fun unreadCount(recipientId: UUID): Int =
        Db.queryFirst(
            "select count(*) as c from notifications n where n.recipient_id = ? and n.read_at is null" + VISIBLE,
            recipientId, *visibleParams(recipientId),
        ) { it.getInt("c") } ?: 0

    /**
     * [upTo] 까지의 알림을 읽음으로. 앱은 자기가 받은 가장 최신 알림의 시각(서버가 준 값)을 보낸다 —
     * 목록을 보는 사이 새로 온 알림까지 읽음 처리하지 않게. null 이면 DB 시각 기준 지금까지.
     * (API 서버·폰 시계는 DB 와 몇 초씩 다를 수 있어 그 시계로 "지금"을 정하지 않는다)
     */
    fun markRead(recipientId: UUID, upTo: OffsetDateTime?): Int =
        Db.update(
            """
            update notifications n set read_at = now()
            where n.recipient_id = ? and n.read_at is null and n.created_at <= coalesce(?::timestamptz, now())
            """.trimIndent() + VISIBLE,
            recipientId, upTo, *visibleParams(recipientId),
        )
}

/**
 * 받는 사람에게 지금 보여도 되는 알림 (별칭 n). `?` 6개 = recipientId ×6 ([visibleParams]).
 * - 만든 사람과 차단 관계가 아니고
 * - 추모 페이지 알림이면 그 페이지를 지금 볼 수 있을 때만 (비공개 전환·개설자와 차단이면 숨김)
 */
private const val VISIBLE = """
    and not exists (
        select 1 from blocks b
        where (b.blocker_id = ? and b.blocked_id = n.actor_id)
           or (b.blocker_id = n.actor_id and b.blocked_id = ?)
    )
    and (n.memorial_id is null or exists (select 1 from memorials m where m.id = n.memorial_id""" + MEMORIAL_VISIBLE + """))
"""

private fun visibleParams(recipientId: UUID): Array<Any?> = Array(6) { recipientId }

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
