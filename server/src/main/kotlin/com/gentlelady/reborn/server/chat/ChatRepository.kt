package com.gentlelady.reborn.server.chat

import com.gentlelady.reborn.server.memorial.GuestbookCursor
import com.gentlelady.reborn.server.plugins.Db
import kotlinx.serialization.Serializable
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.UUID

@Serializable
data class ChatUser(val id: String, val handle: String, val displayName: String, val avatarUrl: String? = null)

@Serializable
data class ConversationResponse(
    val id: String,
    val other: ChatUser,
    val lastMessage: String? = null,
    val lastMessageAt: String? = null,
    val unread: Boolean = false,
)

@Serializable
data class ConversationListResponse(val items: List<ConversationResponse>)

@Serializable
data class MessageResponse(val id: String, val senderId: String, val body: String, val createdAt: String)

@Serializable
data class MessageListResponse(val items: List<MessageResponse>, val nextCursor: String? = null)

@Serializable
data class OpenConversationRequest(val userId: String)

/** [clientId] = 앱이 메시지마다 만든 id. 다시 보내도 한 번만 저장된다 */
@Serializable
data class SendMessageRequest(val body: String, val clientId: String? = null)

/**
 * 1:1 메시지 (V15). 서로 차단한 사이(어느 쪽이든)면 대화방이 안 보이고 보낼 수도 없다 — 풀면 다시 보인다.
 * 내 쪽 열(읽음·숨김)은 내가 user_a 인지 b 인지로 고른다.
 */
class ChatRepository {

    sealed interface Open {
        data class Ok(val conversation: ConversationResponse) : Open
        object NotFound : Open
        object Blocked : Open
    }

    /** 그 사람과의 대화방 (없으면 만든다). 가입을 마친 사람만. */
    fun open(me: UUID, other: UUID): Open {
        val exists = Db.queryFirst("select 1 from profiles where id = ? and onboarded_at is not null", other) { true } == true
        if (!exists) return Open.NotFound
        if (blocked(me, other)) return Open.Blocked
        val (a, b) = if (me.toString() < other.toString()) me to other else other to me
        Db.update("insert into conversations (user_a, user_b) values (?, ?) on conflict (user_a, user_b) do nothing", a, b)
        val id = Db.queryFirst("select id from conversations where user_a = ? and user_b = ?", a, b) {
            it.getObject("id", UUID::class.java)
        }!!
        return Open.Ok(get(me, id)!!)
    }

    /** 내 대화방 목록 (최근 메시지순). 내가 지운 뒤 새 메시지가 없는 방·차단 관계인 방은 빼고. */
    fun list(me: UUID): List<ConversationResponse> =
        Db.query(
            SELECT + """
            where ? in (c.user_a, c.user_b)
              and c.last_message_at is not null
              and c.last_message_at > coalesce(${mine("hidden_before")}, '-infinity')
              and $NOT_BLOCKED
            order by c.last_message_at desc
            limit 100
            """.trimIndent(),
            *selectParams(me), me, me,
            map = ResultSet::toConversation,
        )

    /** 내 대화방 하나. 내 방이 아니거나 차단 관계면 null. */
    fun get(me: UUID, id: UUID): ConversationResponse? =
        Db.queryFirst(
            SELECT + " where c.id = ? and ? in (c.user_a, c.user_b) and $NOT_BLOCKED",
            *selectParams(me), id, me,
            map = ResultSet::toConversation,
        )

    /**
     * 메시지. [before] 가 있으면 그보다 오래된 것(위로 스크롤), [after] 가 있으면 그보다 새 것(새 메시지 확인, 오래된 순).
     * 둘 다 없으면 최신 [limit] 개. 내가 지운 시각 이전 메시지는 안 보인다.
     */
    fun messages(me: UUID, id: UUID, before: GuestbookCursor?, after: GuestbookCursor?, limit: Int): List<MessageResponse> =
        Db.query(
            """
            select m.id, m.sender_id, m.body, m.created_at
            from messages m join conversations c on c.id = m.conversation_id
            where c.id = ? and ? in (c.user_a, c.user_b)
              and m.created_at > coalesce(${mine("hidden_before")}, '-infinity')
              and (?::timestamptz is null or (m.created_at, m.id) < (?::timestamptz, ?::uuid))
              and (?::timestamptz is null or (m.created_at, m.id) > (?::timestamptz, ?::uuid))
            order by m.created_at ${if (after != null) "asc" else "desc"}, m.id ${if (after != null) "asc" else "desc"}
            limit ?
            """.trimIndent(),
            id, me, me,
            before?.createdAt, before?.createdAt, before?.id,
            after?.createdAt, after?.createdAt, after?.id,
            limit,
            map = ResultSet::toMessage,
        )

    /**
     * 보내기. 내 방이 아니거나 차단 관계면 null. 한 문장으로 메시지를 넣고 방의 최근 시각을 올린다.
     * - 방 줄을 잠그고 넣은 시각(clock_timestamp)을 찍는다 → 한 방의 메시지는 저장된 순서 = 시각 순서.
     *   (now() 는 시작 시각이라, 먼저 시작해 늦게 저장된 메시지가 "새 메시지 확인"·안 읽음 계산에서 빠질 수 있었다)
     * - 같은 [clientId] 로 다시 보내면 새로 넣지 않고 처음 것을 돌려준다 (응답만 못 받은 재시도).
     */
    fun send(me: UUID, id: UUID, body: String, clientId: UUID?): MessageResponse? =
        Db.queryFirst(
            """
            with c as (
                select c.id from conversations c
                where c.id = ? and ? in (c.user_a, c.user_b) and $NOT_BLOCKED
                for update of c
            ), m as (
                insert into messages (conversation_id, sender_id, body, client_id, created_at)
                select c.id, ?, ?, ?, clock_timestamp() from c
                on conflict (sender_id, client_id) do nothing
                returning id, conversation_id, sender_id, body, created_at
            ), u as (
                update conversations set last_message_at = greatest(last_message_at, m.created_at),
                    a_read_at = case when user_a = ? then greatest(a_read_at, m.created_at) else a_read_at end,
                    b_read_at = case when user_b = ? then greatest(b_read_at, m.created_at) else b_read_at end
                from m where conversations.id = m.conversation_id
            )
            select id, sender_id, body, created_at from m
            union all
            select id, sender_id, body, created_at from messages
            where conversation_id = ? and sender_id = ? and client_id = ? and not exists (select 1 from m)
            """.trimIndent(),
            id, me, me, body, clientId, me, me, id, me, clientId,
            map = ResultSet::toMessage,
        )

    /**
     * [upTo] (앱이 받은 가장 최신 메시지 시각) 까지 읽음. 없으면 이 방의 마지막 메시지까지.
     * 앞으로만 간다 — 늦게 온 옛 요청이 읽음을 되돌리지 않게.
     */
    fun markRead(me: UUID, id: UUID, upTo: OffsetDateTime?): Boolean =
        Db.update(
            """
            update conversations set
                a_read_at = case when user_a = ? then greatest(a_read_at, coalesce(?::timestamptz, last_message_at)) else a_read_at end,
                b_read_at = case when user_b = ? then greatest(b_read_at, coalesce(?::timestamptz, last_message_at)) else b_read_at end
            where id = ? and ? in (user_a, user_b)
            """.trimIndent(),
            me, upTo, me, upTo, id, me,
        ) == 1

    /**
     * 내 목록에서 지운다 (상대에겐 그대로). 지금까지의 메시지는 나에게 안 보이고, 새 메시지가 오면 다시 나타난다.
     * 기준은 방의 마지막 메시지 시각 (보내기와 같은 줄을 잠그므로 그 뒤에 저장된 메시지는 항상 더 늦다)
     */
    fun hide(me: UUID, id: UUID): Boolean =
        Db.update(
            """
            update conversations set
                a_hidden_before = case when user_a = ? then last_message_at else a_hidden_before end,
                b_hidden_before = case when user_b = ? then last_message_at else b_hidden_before end,
                a_read_at = case when user_a = ? then greatest(a_read_at, last_message_at) else a_read_at end,
                b_read_at = case when user_b = ? then greatest(b_read_at, last_message_at) else b_read_at end
            where id = ? and ? in (user_a, user_b)
            """.trimIndent(),
            me, me, me, me, id, me,
        ) == 1

    private fun blocked(me: UUID, other: UUID): Boolean =
        Db.queryFirst(
            "select 1 from blocks where (blocker_id = ? and blocked_id = ?) or (blocker_id = ? and blocked_id = ?)",
            me, other, other, me,
        ) { true } == true
}

/** 내 쪽 열 (c.a_x / c.b_x). 첫 `?` = 나 */
private fun mine(column: String) = "(case when c.user_a = ? then c.a_$column else c.b_$column end)"

private const val NOT_BLOCKED = """not exists (
    select 1 from blocks b
    where (b.blocker_id = c.user_a and b.blocked_id = c.user_b) or (b.blocker_id = c.user_b and b.blocked_id = c.user_a)
)"""

/** `?` 순서: 상대 고르기(나), 안 읽음 계산(나 ×3) */
private val SELECT = """
    select c.id, o.id as other_id, o.handle, o.display_name, o.avatar_url,
           lm.body as last_body, lm.created_at as last_at,
           exists (
               select 1 from messages x
               where x.conversation_id = c.id and x.sender_id <> ?
                 and x.created_at > coalesce(${mine("read_at")}, '-infinity')
                 and x.created_at > coalesce(${mine("hidden_before")}, '-infinity')
           ) as unread
    from conversations c
    join profiles o on o.id = (case when c.user_a = ? then c.user_b else c.user_a end)
    left join lateral (
        select m.body, m.created_at from messages m
        where m.conversation_id = c.id
        order by m.created_at desc, m.id desc limit 1
    ) lm on true
""".trimIndent()

private fun selectParams(me: UUID) = arrayOf<Any?>(me, me, me, me)

private fun ResultSet.toConversation() = ConversationResponse(
    id = getString("id"),
    other = ChatUser(getString("other_id"), getString("handle"), getString("display_name"), getString("avatar_url")),
    lastMessage = getString("last_body"),
    lastMessageAt = getObject("last_at", OffsetDateTime::class.java)?.toString(),
    unread = getBoolean("unread"),
)

private fun ResultSet.toMessage() = MessageResponse(
    id = getString("id"),
    senderId = getString("sender_id"),
    body = getString("body"),
    createdAt = getObject("created_at", OffsetDateTime::class.java).toString(),
)
