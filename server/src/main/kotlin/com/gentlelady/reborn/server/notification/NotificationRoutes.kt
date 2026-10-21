package com.gentlelady.reborn.server.notification

import com.gentlelady.reborn.server.memorial.GuestbookCursor
import com.gentlelady.reborn.server.plugins.ErrorResponse
import com.gentlelady.reborn.server.plugins.SUPABASE_AUTH
import com.gentlelady.reborn.server.plugins.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

private const val DEFAULT_LIMIT = 30
private const val MAX_LIMIT = 100

@Serializable
data class MarkReadResponse(val updated: Int)

fun Route.notificationRoutes(repo: NotificationRepository) {
    authenticate(SUPABASE_AUTH) {
        get("/notifications") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, MAX_LIMIT) ?: DEFAULT_LIMIT
            val cursor = call.request.queryParameters["cursor"]?.let(GuestbookCursor::parse)
            val items = repo.list(call.userId(), cursor, limit)
            val next = items.takeIf { it.size == limit }?.last()?.let { GuestbookCursor.of(it.createdAt, it.id).toString() }
            call.respond(NotificationListResponse(items, next))
        }
        get("/notifications/unread-count") {
            call.respond(UnreadCountResponse(repo.unreadCount(call.userId())))
        }
        /** ?upTo=받은 가장 최신 알림의 createdAt (ISO-8601). 없으면 지금까지. */
        post("/notifications/read") {
            val upTo = call.request.queryParameters["upTo"]?.let {
                runCatching { OffsetDateTime.parse(it) }.getOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("bad_up_to"))
            }
            call.respond(MarkReadResponse(repo.markRead(call.userId(), upTo)))
        }
    }
}
