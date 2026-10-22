package com.gentlelady.reborn.server.profile

import com.gentlelady.reborn.server.plugins.ErrorResponse
import com.gentlelady.reborn.server.plugins.SUPABASE_AUTH
import com.gentlelady.reborn.server.plugins.userId
import com.gentlelady.reborn.server.upload.imageListError
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

fun Route.profileRoutes(repo: ProfileRepository, views: com.gentlelady.reborn.server.search.SearchRepository) {
    authenticate(SUPABASE_AUTH) {
        get("/me/profile") {
            repo.getMy(call.userId())?.let { call.respond(it) }
                ?: call.respond(HttpStatusCode.NotFound, ErrorResponse("profile_not_found"))
        }

        patch("/me/profile") {
            val userId = call.userId()
            val req = call.receive<UpdateProfileRequest>()
            // 지금 아바타 그대로 다시 보내는 건 허용 (붙일 수 있는 시간이 지났어도)
            val newAvatar = req.avatarUrl?.takeIf { it != repo.getMy(userId)?.avatarUrl }
            imageListError(userId, listOfNotNull(newAvatar))?.let {
                return@patch call.respond(HttpStatusCode.BadRequest, ErrorResponse(it))
            }
            profileTextError(req)?.let {
                return@patch call.respond(HttpStatusCode.BadRequest, ErrorResponse(it))
            }
            val handle = req.handle?.trim()
            if (handle != null && (handle.isEmpty() || repo.handleTaken(handle, userId))) {
                return@patch call.respond(HttpStatusCode.Conflict, ErrorResponse("handle_taken"))
            }
            val cleaned = req.copy(handle = handle, displayName = req.displayName?.trim(), bio = req.bio?.trim())
            repo.updateMy(userId, cleaned)?.let { call.respond(it) }
                ?: call.respond(HttpStatusCode.NotFound, ErrorResponse("profile_not_found"))
        }

        /** 이름·아이디로 사람 찾기 (메시지 보낼 사람). 가입을 마친 사람만, 나·차단 관계는 빼고 */
        get("/users/search") {
            val q = call.request.queryParameters["q"].orEmpty().trim()
            if (q.isEmpty() || q.length > 30) return@get respondUserList(emptyList())
            respondUserList(repo.search(call.userId(), q, page()))
        }

        route("/users/{id}") {
            get {
                val target = UUID.fromString(call.parameters["id"])
                val profile = repo.getPublic(call.userId(), target)
                    ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("user_not_found"))
                // 남이 열었으면 조회수 (세다 실패해도 프로필은 보여 준다)
                if (target != call.userId()) runCatching { views.countView(call.userId(), "profile", target) }
                call.respond(profile)
            }
            get("/followers") {
                when (repo.canSeeRelations(call.userId(), targetId())) {
                    null -> call.respond(HttpStatusCode.NotFound, ErrorResponse("user_not_found"))
                    false -> respondUserList(emptyList())
                    true -> respondUserList(repo.followers(call.userId(), targetId(), page()))
                }
            }
            get("/following") {
                when (repo.canSeeRelations(call.userId(), targetId())) {
                    null -> call.respond(HttpStatusCode.NotFound, ErrorResponse("user_not_found"))
                    false -> respondUserList(emptyList())
                    true -> respondUserList(repo.following(call.userId(), targetId(), page()))
                }
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

/** 닉네임·한 줄 소개 길이 검사 (앞뒤 공백 제외). 문제 있으면 에러 코드. 앱도 같은 한도로 막지만 서버가 최종. */
internal fun profileTextError(req: UpdateProfileRequest): String? {
    val name = req.displayName?.trim()
    val bio = req.bio?.trim()
    return when {
        name != null && name.isEmpty() -> "empty_display_name"
        name != null && name.length > MAX_DISPLAY_NAME -> "display_name_too_long"
        bio != null && bio.length > MAX_BIO -> "bio_too_long"
        else -> null
    }
}

const val MAX_DISPLAY_NAME = 20
const val MAX_BIO = 100
