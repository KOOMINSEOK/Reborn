package com.gentlelady.reborn.server.profile

import com.gentlelady.reborn.server.plugins.ErrorResponse
import com.gentlelady.reborn.server.plugins.SUPABASE_AUTH
import com.gentlelady.reborn.server.plugins.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route
import java.util.UUID

private const val DEFAULT_LIMIT = 30
private const val MAX_LIMIT = 100

fun Route.profileRoutes(repo: ProfileRepository) {
    authenticate(SUPABASE_AUTH) {
        get("/me/profile") {
            repo.getMy(call.userId())?.let { call.respond(it) }
                ?: call.respond(HttpStatusCode.NotFound, ErrorResponse("profile_not_found"))
        }

        patch("/me/profile") {
            val userId = call.userId()
            val req = call.receive<UpdateProfileRequest>()
            val handle = req.handle?.trim()
            if (handle != null && (handle.isEmpty() || repo.handleTaken(handle, userId))) {
                return@patch call.respond(HttpStatusCode.Conflict, ErrorResponse("handle_taken"))
            }
            repo.updateMy(userId, req.copy(handle = handle))?.let { call.respond(it) }
                ?: call.respond(HttpStatusCode.NotFound, ErrorResponse("profile_not_found"))
        }

        route("/users/{id}") {
            get {
                val target = UUID.fromString(call.parameters["id"])
                repo.getPublic(call.userId(), target)?.let { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, ErrorResponse("user_not_found"))
            }
            get("/followers") {
                respondUserList(repo.followers(call.userId(), targetId(), page()))
            }
            get("/following") {
                respondUserList(repo.following(call.userId(), targetId(), page()))
            }
        }
    }
}

private fun RoutingContext.targetId(): UUID = UUID.fromString(call.parameters["id"])

private fun RoutingContext.page(): Page {
    val offset = call.request.queryParameters["offset"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
    val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, MAX_LIMIT) ?: DEFAULT_LIMIT
    return Page(offset, limit)
}

private suspend fun RoutingContext.respondUserList(items: List<UserSummaryResponse>) {
    val page = page()
    val next = if (items.size == page.limit) page.offset + page.limit else null
    call.respond(UserListResponse(items, next))
}
