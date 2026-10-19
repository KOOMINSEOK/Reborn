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
 * [canView] (viewerId, parentId) — 부모를 볼 수 없으면 좋아요/댓글도 404 (존재 여부 비노출).
 */
fun Route.interactionRoutes(
    parentPath: String,
    commentPath: String,
    repo: InteractionRepo,
    canView: (UUID, UUID) -> Boolean,
) {
    post("$parentPath/{id}/like") {
        val id = UUID.fromString(call.parameters["id"])
        if (!canView(call.userId(), id)) {
            return@post call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found"))
        }
        call.respond(repo.like(call.userId(), id))
    }
    delete("$parentPath/{id}/like") {
        val id = UUID.fromString(call.parameters["id"])
        if (!canView(call.userId(), id)) {
            return@delete call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found"))
        }
        call.respond(repo.unlike(call.userId(), id))
    }
    get("$parentPath/{id}/comments") {
        val id = UUID.fromString(call.parameters["id"])
        if (!canView(call.userId(), id)) {
            return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found"))
        }
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
        if (!canView(call.userId(), id)) {
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
