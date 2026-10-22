package com.gentlelady.reborn.server.chat

import com.gentlelady.reborn.server.memorial.GuestbookCursor
import com.gentlelady.reborn.server.plugins.ErrorResponse
import com.gentlelady.reborn.server.plugins.SUPABASE_AUTH
import com.gentlelady.reborn.server.plugins.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import java.util.UUID

private const val DEFAULT_LIMIT = 30
private const val MAX_LIMIT = 100
const val MAX_MESSAGE_LENGTH = 2000

fun Route.chatRoutes(repo: ChatRepository) {
    authenticate(SUPABASE_AUTH) {
        get("/conversations") {
            call.respond(ConversationListResponse(repo.list(call.userId())))
        }

        /** 그 사람과의 대화방을 연다 (없으면 만든다) */
        post("/conversations") {
            val me = call.userId()
            val other = runCatching { UUID.fromString(call.receive<OpenConversationRequest>().userId) }.getOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("bad_user_id"))
            if (other == me) return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("self"))
            when (val r = repo.open(me, other)) {
                is ChatRepository.Open.Ok -> call.respond(r.conversation)
                ChatRepository.Open.NotFound -> call.respond(HttpStatusCode.NotFound, ErrorResponse("user_not_found"))
                ChatRepository.Open.Blocked -> call.respond(HttpStatusCode.Forbidden, ErrorResponse("blocked"))
            }
        }

        get("/conversations/{id}") {
            val id = call.conversationId() ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("conversation_not_found"))
            repo.get(call.userId(), id)?.let { call.respond(it) }
                ?: call.respond(HttpStatusCode.NotFound, ErrorResponse("conversation_not_found"))
        }

        /** ?before=커서(더 오래된 것) 또는 ?after=커서(새로 온 것, 오래된 순). 둘 다 없으면 최신부터 */
        get("/conversations/{id}/messages") {
            val me = call.userId()
            val id = call.conversationId() ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("conversation_not_found"))
            if (repo.get(me, id) == null) return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("conversation_not_found"))
            val q = call.request.queryParameters
            val before = runCatching { q["before"]?.let(GuestbookCursor::parse) }.getOrElse {
                return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("bad_cursor"))
            }
            val after = runCatching { q["after"]?.let(GuestbookCursor::parse) }.getOrElse {
                return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("bad_cursor"))
            }
            val limit = q["limit"]?.toIntOrNull()?.coerceIn(1, MAX_LIMIT) ?: DEFAULT_LIMIT
            val items = repo.messages(me, id, before, after, limit)
            // 위로 더 볼 커서는 최신부터 받을 때만 (after 는 매번 받은 마지막 것 다음부터 다시 묻는다)
            val next = items.takeIf { after == null && it.size == limit }?.last()?.let { GuestbookCursor.of(it.createdAt, it.id).toString() }
            call.respond(MessageListResponse(items, next))
        }

        post("/conversations/{id}/messages") {
            val id = call.conversationId() ?: return@post call.respond(HttpStatusCode.NotFound, ErrorResponse("conversation_not_found"))
            val body = call.receive<SendMessageRequest>().body.trim()
            if (body.isEmpty() || body.length > MAX_MESSAGE_LENGTH) {
                return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("bad_body"))
            }
            repo.send(call.userId(), id, body)?.let { call.respond(HttpStatusCode.Created, it) }
                // 내 방이 아니거나 차단 관계 — 어느 쪽인지 알려주지 않는다
                ?: call.respond(HttpStatusCode.Forbidden, ErrorResponse("cannot_send"))
        }

        post("/conversations/{id}/read") {
            val id = call.conversationId() ?: return@post call.respond(HttpStatusCode.NotFound, ErrorResponse("conversation_not_found"))
            if (repo.markRead(call.userId(), id)) call.respond(HttpStatusCode.NoContent)
            else call.respond(HttpStatusCode.NotFound, ErrorResponse("conversation_not_found"))
        }

        /** 내 목록에서 지우기 (상대에겐 그대로) */
        delete("/conversations/{id}") {
            val id = call.conversationId() ?: return@delete call.respond(HttpStatusCode.NotFound, ErrorResponse("conversation_not_found"))
            if (repo.hide(call.userId(), id)) call.respond(HttpStatusCode.NoContent)
            else call.respond(HttpStatusCode.NotFound, ErrorResponse("conversation_not_found"))
        }
    }
}

private fun io.ktor.server.application.ApplicationCall.conversationId(): UUID? =
    runCatching { UUID.fromString(parameters["id"]) }.getOrNull()
