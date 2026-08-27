package com.gentlelady.reborn.server.memorial

import com.gentlelady.reborn.server.interaction.InteractionRepo
import com.gentlelady.reborn.server.interaction.interactionRoutes
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

private const val DEFAULT_LIMIT = 20
private const val MAX_LIMIT = 50

fun Route.memorialRoutes(repo: MemorialRepository, historyInteractions: InteractionRepo) {
    authenticate(SUPABASE_AUTH) {
        memorialEndpoints(repo)
        historyEndpoints(repo)
        interactionRoutes("/history", "/history-comments", historyInteractions)
    }
}

private fun Route.memorialEndpoints(repo: MemorialRepository) {
    post("/memorials") {
        call.respond(HttpStatusCode.Created, repo.create(call.userId(), call.receive()))
    }
    get("/memorials/{id}") {
        val memorial = repo.get(UUID.fromString(call.parameters["id"]))
            ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("memorial_not_found"))
        call.respond(memorial)
    }
    post("/memorials/{id}/follow") {
        repo.follow(call.userId(), UUID.fromString(call.parameters["id"]))
        call.respond(HttpStatusCode.NoContent)
    }
    delete("/memorials/{id}/follow") {
        repo.unfollow(call.userId(), UUID.fromString(call.parameters["id"]))
        call.respond(HttpStatusCode.NoContent)
    }
}

private fun Route.historyEndpoints(repo: MemorialRepository) {
    post("/memorials/{id}/history") {
        val memorialId = UUID.fromString(call.parameters["id"])
        if (!repo.exists(memorialId)) {
            return@post call.respond(HttpStatusCode.NotFound, ErrorResponse("memorial_not_found"))
        }
        call.respond(HttpStatusCode.Created, repo.createHistory(memorialId, call.userId(), call.receive()))
    }
    get("/memorials/{id}/history") {
        val memorialId = UUID.fromString(call.parameters["id"])
        val offset = call.request.queryParameters["offset"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
        val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, MAX_LIMIT) ?: DEFAULT_LIMIT
        val items = repo.listHistory(call.userId(), memorialId, offset, limit)
        call.respond(HistoryListResponse(items, if (items.size == limit) offset + limit else null))
    }
    get("/history/{id}") {
        val history = repo.getHistory(call.userId(), UUID.fromString(call.parameters["id"]))
            ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("history_not_found"))
        call.respond(history)
    }
}
