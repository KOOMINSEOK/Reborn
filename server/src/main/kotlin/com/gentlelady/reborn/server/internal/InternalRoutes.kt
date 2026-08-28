package com.gentlelady.reborn.server.internal

import com.gentlelady.reborn.server.Env
import com.gentlelady.reborn.server.plugins.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

private const val SECRET_HEADER = "X-Internal-Secret"

/**
 * 내부 운영용. Supabase JWT 가 아니라 공유 시크릿(INTERNAL_SECRET)으로 보호한다.
 * Cloud Scheduler 가 매분 POST /internal/publish-due 를 호출한다.
 */
fun Route.internalRoutes() {
    post("/internal/publish-due") {
        val secret = Env.get("INTERNAL_SECRET")
        if (secret.isNullOrBlank() || call.request.header(SECRET_HEADER) != secret) {
            return@post call.respond(HttpStatusCode.Forbidden, ErrorResponse("forbidden"))
        }
        call.respond(PublishDueResponse(published = PublishService.publishDue()))
    }
}
