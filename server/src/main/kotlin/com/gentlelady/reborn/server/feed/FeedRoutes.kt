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

private const val DEFAULT_LIMIT = 20
private const val MAX_LIMIT = 50
private const val MAX_FEED_SCAN = 100
private const val FOLLOWING_TO_RECOMMENDED = 3

fun Route.feedRoutes(repo: FeedRepository) {
    authenticate(SUPABASE_AUTH) {
        memorialEndpoints(repo)
        postEndpoints(repo)
        feedEndpoint(repo)
    }
}

private fun Route.memorialEndpoints(repo: FeedRepository) {
    post("/memorials") {
        val req = call.receive<CreateMemorialRequest>()
        call.respond(HttpStatusCode.Created, repo.createMemorial(call.userId(), req))
    }
    get("/memorials/{id}") {
        val memorial = repo.getMemorial(UUID.fromString(call.parameters["id"]))
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

private fun Route.postEndpoints(repo: FeedRepository) {
    post("/posts") {
        val req = call.receive<CreatePostRequest>()
        if (!repo.memorialExists(UUID.fromString(req.memorialId))) {
            return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("memorial_not_found"))
        }
        call.respond(HttpStatusCode.Created, repo.createPost(call.userId(), req))
    }
}

private fun Route.feedEndpoint(repo: FeedRepository) {
    get("/feed") {
        val userId = call.userId()
        val offset = call.request.queryParameters["offset"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
        val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, MAX_LIMIT) ?: DEFAULT_LIMIT
        val scan = (offset + limit).coerceIn(limit, MAX_FEED_SCAN)

        val merged = interleave(
            following = repo.followingFeed(userId, scan),
            recommended = repo.recommendedFeed(userId, scan),
        )
        val nextOffset = if (merged.size > offset + limit) offset + limit else null
        call.respond(FeedResponse(items = merged.drop(offset).take(limit), nextOffset = nextOffset))
    }
}

/** following:recommended = 3:1 로 섞는다. */
internal fun interleave(following: List<PostResponse>, recommended: List<PostResponse>): List<PostResponse> {
    val out = ArrayList<PostResponse>(following.size + recommended.size)
    val f = following.iterator()
    val r = recommended.iterator()
    while (f.hasNext() || r.hasNext()) {
        repeat(FOLLOWING_TO_RECOMMENDED) { if (f.hasNext()) out.add(f.next()) }
        if (r.hasNext()) out.add(r.next())
    }
    return out
}
