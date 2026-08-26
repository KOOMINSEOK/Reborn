package com.gentlelady.reborn.server.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        get("/health") {
            call.respond(HealthResponse(status = "ok", db = Db.status()))
        }

        authenticate(SUPABASE_AUTH) {
            get("/me") {
                val user = call.principal<UserPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ErrorResponse("unauthorized"))
                call.respond(MeResponse(userId = user.id, email = user.email))
            }
        }
    }
}
