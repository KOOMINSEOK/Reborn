package com.gentlelady.reborn.server.memorial

import com.gentlelady.reborn.server.interaction.InteractionRepo
import com.gentlelady.reborn.server.interaction.interactionRoutes
import com.gentlelady.reborn.server.plugins.ErrorResponse
import com.gentlelady.reborn.server.plugins.SUPABASE_AUTH
import com.gentlelady.reborn.server.plugins.userId
import com.gentlelady.reborn.server.upload.imageListError
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
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
        guestbookEndpoints(repo)
        myGuestbookEndpoint(repo)
        interactionRoutes("/history", "/history-comments", historyInteractions) { viewer, id ->
            repo.getHistory(viewer, id) != null
        }
    }
}

private fun Route.memorialEndpoints(repo: MemorialRepository) {
    post("/memorials") {
        val req = call.receive<CreateMemorialRequest>()
        imageListError(call.userId(), listOfNotNull(req.profileImageUrl))?.let {
            return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse(it))
        }
        call.respond(HttpStatusCode.Created, repo.create(call.userId(), req))
    }
    get("/memorials/{id}") {
        val memorial = repo.get(call.userId(), UUID.fromString(call.parameters["id"]))
            ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("memorial_not_found"))
        call.respond(memorial)
    }
    post("/memorials/{id}/follow") {
        val memorialId = UUID.fromString(call.parameters["id"])
        if (!repo.canView(call.userId(), memorialId)) {
            return@post call.respond(HttpStatusCode.NotFound, ErrorResponse("memorial_not_found"))
        }
        repo.follow(call.userId(), memorialId)
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
        if (!repo.canView(call.userId(), memorialId)) {
            return@post call.respond(HttpStatusCode.NotFound, ErrorResponse("memorial_not_found"))
        }
        val req = call.receive<CreateHistoryRequest>()
        imageListError(call.userId(), req.imageUrls)?.let {
            return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse(it))
        }
        call.respond(HttpStatusCode.Created, repo.createHistory(memorialId, call.userId(), req))
    }
    get("/memorials/{id}/history") {
        val memorialId = UUID.fromString(call.parameters["id"])
        if (!repo.canView(call.userId(), memorialId)) {
            return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("memorial_not_found"))
        }
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

private fun Route.guestbookEndpoints(repo: MemorialRepository) {
    post("/memorials/{id}/guestbook") {
        val memorialId = UUID.fromString(call.parameters["id"])
        val message = call.receive<CreateGuestbookRequest>().message.trim()
        if (message.isEmpty()) {
            return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("empty_message"))
        }
        if (!repo.canView(call.userId(), memorialId)) {
            return@post call.respond(HttpStatusCode.NotFound, ErrorResponse("memorial_not_found"))
        }
        call.respond(HttpStatusCode.Created, repo.createGuestbookEntry(memorialId, call.userId(), message))
    }
    get("/memorials/{id}/guestbook") {
        val memorialId = UUID.fromString(call.parameters["id"])
        if (!repo.canView(call.userId(), memorialId)) {
            return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("memorial_not_found"))
        }
        val offset = call.request.queryParameters["offset"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
        val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, MAX_LIMIT) ?: DEFAULT_LIMIT
        guestbookPage(repo, memorialId, call.request.queryParameters, offset, limit)
            ?.let { call.respond(it) }
            ?: call.respond(HttpStatusCode.NotFound, ErrorResponse("guestbook_entry_not_found"))
    }
    delete("/guestbook/{id}") {
        val removed = repo.deleteGuestbookEntry(UUID.fromString(call.parameters["id"]), call.userId())
        if (removed == 0) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("guestbook_entry_not_found"))
        } else {
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

/** 내가 남긴 방명록 (모든 추모 페이지, 최신순, 커서). */
private fun Route.myGuestbookEndpoint(repo: MemorialRepository) {
    get("/me/guestbook") {
        val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, MAX_LIMIT) ?: DEFAULT_LIMIT
        val cursor = call.request.queryParameters["cursor"]?.let(GuestbookCursor::parse)
        val items = repo.listMyGuestbook(call.userId(), cursor, limit)
        val next = items.takeIf { it.size == limit }?.last()?.let { GuestbookCursor.of(it.createdAt, it.id).toString() }
        call.respond(MyGuestbookListResponse(items, next))
    }
}

/**
 * 방명록 한 페이지. around = 이 방명록을 가운데로 앞뒤 글 / after = 그보다 최신 글 / cursor = 그보다 이전 글.
 * 형식 오류는 400(IllegalArgumentException), around 의 글이 없으면 null(→ 404).
 */
private fun guestbookPage(
    repo: MemorialRepository,
    memorialId: UUID,
    q: Parameters,
    offset: Int,
    limit: Int,
): GuestbookListResponse? {
    val around = q["around"]?.let(UUID::fromString)
    val after = q["after"]?.let(GuestbookCursor::parse)
    return when {
        around != null -> repo.getGuestbookEntry(memorialId, around)?.let {
            guestbookAround(repo, memorialId, it, half = (limit / 2).coerceAtLeast(1))
        }
        after != null -> repo.listGuestbookNewer(memorialId, after, limit).let { newer ->
            GuestbookListResponse(
                items = newer,
                newerCursor = if (newer.size == limit) GuestbookCursor.after(newer.first()).toString() else null,
            )
        }
        else -> {
            val cursor = q["cursor"]?.let(GuestbookCursor::parse)
            val items = repo.listGuestbook(memorialId, cursor, if (cursor != null) 0 else offset, limit)
            val full = items.size == limit
            GuestbookListResponse(
                items = items,
                nextOffset = if (full) offset + limit else null,
                nextCursor = if (full) GuestbookCursor.after(items.last()).toString() else null,
            )
        }
    }
}

/** [entry] 를 가운데로 최신 쪽 [half] 개 + 이전 쪽 [half] 개 (최신순). 양쪽에 더 있으면 각 방향 커서. */
private fun guestbookAround(
    repo: MemorialRepository,
    memorialId: UUID,
    entry: GuestbookEntryResponse,
    half: Int,
): GuestbookListResponse {
    val pivot = GuestbookCursor.after(entry)
    val newer = repo.listGuestbookNewer(memorialId, pivot, half)
    val older = repo.listGuestbook(memorialId, pivot, 0, half)
    return GuestbookListResponse(
        items = newer + entry + older,
        nextCursor = if (older.size == half) GuestbookCursor.after(older.last()).toString() else null,
        newerCursor = if (newer.size == half) GuestbookCursor.after(newer.first()).toString() else null,
    )
}
