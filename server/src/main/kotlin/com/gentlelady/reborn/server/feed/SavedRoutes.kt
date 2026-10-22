package com.gentlelady.reborn.server.feed

import com.gentlelady.reborn.server.memorial.HistoryListResponse
import com.gentlelady.reborn.server.memorial.MemorialRepository
import com.gentlelady.reborn.server.plugins.ErrorResponse
import com.gentlelady.reborn.server.plugins.SUPABASE_AUTH
import com.gentlelady.reborn.server.plugins.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import java.util.UUID

private const val DEFAULT_LIMIT = 30
private const val MAX_LIMIT = 100

/** 저장(게시물·추억 글)과 내 게시물 보관함 */
fun Route.savedRoutes(feed: FeedRepository, memorials: MemorialRepository) {
    authenticate(SUPABASE_AUTH) {
        put("/posts/{id}/save") { respondSaved(feed.setSaved(call.userId(), id() ?: return@put notFound(), true)) }
        delete("/posts/{id}/save") { respondSaved(feed.setSaved(call.userId(), id() ?: return@delete notFound(), false)) }
        put("/history/{id}/save") { respondSaved(memorials.setHistorySaved(call.userId(), id() ?: return@put notFound(), true)) }
        delete("/history/{id}/save") { respondSaved(memorials.setHistorySaved(call.userId(), id() ?: return@delete notFound(), false)) }

        get("/me/saved/posts") {
            val (offset, limit) = page()
            val items = feed.savedPosts(call.userId(), offset, limit)
            call.respond(FeedResponse(items = items, nextOffset = if (items.size == limit) offset + limit else null))
        }
        get("/me/saved/history") {
            val (offset, limit) = page()
            val items = memorials.savedHistory(call.userId(), offset, limit)
            call.respond(HistoryListResponse(items = items, nextOffset = if (items.size == limit) offset + limit else null))
        }

        /** 내 글 보관 (피드·프로필에서 숨김) / 되돌리기. 남의 글이면 404 */
        post("/posts/{id}/archive") {
            if (feed.setArchived(call.userId(), id() ?: return@post notFound(), true)) call.respond(HttpStatusCode.NoContent) else notFound()
        }
        delete("/posts/{id}/archive") {
            if (feed.setArchived(call.userId(), id() ?: return@delete notFound(), false)) call.respond(HttpStatusCode.NoContent) else notFound()
        }
        get("/me/archive") {
            val (offset, limit) = page()
            val items = feed.archivedPosts(call.userId(), offset, limit)
            call.respond(FeedResponse(items = items, nextOffset = if (items.size == limit) offset + limit else null))
        }
    }
}

private fun RoutingContext.id(): UUID? = runCatching { UUID.fromString(call.parameters["id"]) }.getOrNull()

private suspend fun RoutingContext.notFound() = call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found"))

/** 저장은 볼 수 있는 글만 — 못 보면 없는 것처럼 404 */
private suspend fun RoutingContext.respondSaved(ok: Boolean) =
    if (ok) call.respond(HttpStatusCode.NoContent) else notFound()

private fun RoutingContext.page(): Pair<Int, Int> {
    val q = call.request.queryParameters
    return (q["offset"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0) to (q["limit"]?.toIntOrNull()?.coerceIn(1, MAX_LIMIT) ?: DEFAULT_LIMIT)
}
