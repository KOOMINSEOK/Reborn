package com.gentlelady.reborn.server.search

import com.gentlelady.reborn.server.feed.FeedRepository
import com.gentlelady.reborn.server.feed.FeedResponse
import com.gentlelady.reborn.server.plugins.SUPABASE_AUTH
import com.gentlelady.reborn.server.plugins.userId
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

private const val DEFAULT_LIMIT = 30
private const val MAX_LIMIT = 50

fun Route.searchRoutes(repo: SearchRepository, feed: FeedRepository) {
    authenticate(SUPABASE_AUTH) {
        /** ?q=검색어&sort=likes|views|flowers */
        get("/search") {
            val q = call.request.queryParameters["q"].orEmpty().trim()
            if (q.isEmpty() || q.length > 30) return@get call.respond(SearchResponse(emptyList()))
            val sort = when (call.request.queryParameters["sort"]) {
                "views" -> SearchSort.VIEWS
                "flowers" -> SearchSort.FLOWERS
                else -> SearchSort.LIKES
            }
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, MAX_LIMIT) ?: DEFAULT_LIMIT
            call.respond(SearchResponse(repo.search(call.userId(), q, sort, limit)))
        }

        /** 검색어가 없을 때 보여 줄 아무 게시물 몇 개 (볼 수 있는 것만) */
        get("/search/random") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, 10) ?: 3
            call.respond(FeedResponse(items = feed.randomPosts(call.userId(), limit), nextOffset = null))
        }
    }
}
