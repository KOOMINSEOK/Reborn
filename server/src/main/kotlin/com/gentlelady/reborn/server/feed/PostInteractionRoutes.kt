package com.gentlelady.reborn.server.feed

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

private const val DEFAULT_COMMENT_LIMIT = 30
private const val MAX_COMMENT_LIMIT = 100

fun Route.postInteractionRoutes(repo: PostInteractionRepository) {
    authenticate(SUPABASE_AUTH) {
        likeEndpoints(repo)
        commentEndpoints(repo)
    }
}

private fun Route.likeEndpoints(repo: PostInteractionRepository) {
    post("/posts/{id}/like") {
        val postId = UUID.fromString(call.parameters["id"])
        if (!repo.postExists(postId)) {
            return@post call.respond(HttpStatusCode.NotFound, ErrorResponse("post_not_found"))
        }
        call.respond(repo.like(call.userId(), postId))
    }
    delete("/posts/{id}/like") {
        call.respond(repo.unlike(call.userId(), UUID.fromString(call.parameters["id"])))
    }
}

private fun Route.commentEndpoints(repo: PostInteractionRepository) {
    get("/posts/{id}/comments") {
        val postId = UUID.fromString(call.parameters["id"])
        val offset = call.request.queryParameters["offset"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
        val limit = call.request.queryParameters["limit"]?.toIntOrNull()
            ?.coerceIn(1, MAX_COMMENT_LIMIT) ?: DEFAULT_COMMENT_LIMIT
        val items = repo.listComments(postId, offset, limit)
        val nextOffset = if (items.size == limit) offset + limit else null
        call.respond(CommentListResponse(items, nextOffset))
    }
    post("/posts/{id}/comments") {
        val postId = UUID.fromString(call.parameters["id"])
        val body = call.receive<CreateCommentRequest>().body.trim()
        if (body.isEmpty()) {
            return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("empty_comment"))
        }
        if (!repo.postExists(postId)) {
            return@post call.respond(HttpStatusCode.NotFound, ErrorResponse("post_not_found"))
        }
        call.respond(HttpStatusCode.Created, repo.addComment(call.userId(), postId, body))
    }
    delete("/comments/{id}") {
        val removed = repo.deleteComment(UUID.fromString(call.parameters["id"]), call.userId())
        if (removed == 0) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("comment_not_found"))
        } else {
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
