package com.gentlelady.reborn.server.interaction

import com.gentlelady.reborn.server.plugins.ErrorResponse
import com.gentlelady.reborn.server.plugins.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import java.util.UUID

private const val DEFAULT_LIMIT = 30
private const val MAX_LIMIT = 100

/**
 * `{parentPath}/{id}/like` · `{parentPath}/{id}/comments` · `{commentPath}/{id}` 를 건다.
 * 반드시 authenticate 블록 안에서 호출한다.
 */
fun Route.interactionRoutes(parentPath: String, commentPath: String, repo: InteractionRepo) {
    post("$parentPath/{id}/like") {
        val id = UUID.fromString(call.parameters["id"])
        if (!repo.parentExists(id)) {
            return@post call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found"))
        }
        call.respond(repo.like(call.userId(), id))
    }
    delete("$parentPath/{id}/like") {
        call.respond(repo.unlike(call.userId(), UUID.fromString(call.parameters["id"])))
    }
    get("$parentPath/{id}/comments") {
        val id = UUID.fromString(call.parameters["id"])
        val offset = call.request.queryParameters["offset"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
        val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, MAX_LIMIT) ?: DEFAULT_LIMIT
        val items = repo.listComments(id, offset, limit)
        call.respond(CommentListResponse(items, if (items.size == limit) offset + limit else null))
    }
    post("$parentPath/{id}/comments") {
        val id = UUID.fromString(call.parameters["id"])
        val body = call.receive<CreateCommentRequest>().body.trim()
        if (body.isEmpty()) {
            return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("empty_comment"))
        }
        if (!repo.parentExists(id)) {
            return@post call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found"))
        }
        call.respond(HttpStatusCode.Created, repo.addComment(call.userId(), id, body))
    }
    delete("$commentPath/{id}") {
        val removed = repo.deleteComment(UUID.fromString(call.parameters["id"]), call.userId())
        if (removed == 0) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found"))
        } else {
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
