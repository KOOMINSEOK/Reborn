package com.gentlelady.reborn.server.plugins

import com.gentlelady.reborn.server.feed.FeedRepository
import com.gentlelady.reborn.server.feed.feedRoutes
import com.gentlelady.reborn.server.interaction.InteractionRepo
import com.gentlelady.reborn.server.interaction.interactionRoutes
import com.gentlelady.reborn.server.memorial.MemorialRepository
import com.gentlelady.reborn.server.memorial.memorialRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    val feed = FeedRepository()
    val memorial = MemorialRepository()
    val postInteractions = InteractionRepo("posts", "post_likes", "post_comments", "post_id")
    val historyInteractions = InteractionRepo("memorial_history", "history_likes", "history_comments", "history_id")

    routing {
        get("/health") {
            call.respond(HealthResponse(status = "ok", db = Db.status()))
        }

        // 시드용 더미 이미지. resources/static/seed/*.png → /static/seed/*.png
        staticResources("/static", "static")

        authenticate(SUPABASE_AUTH) {
            get("/me") {
                val user = call.principal<UserPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ErrorResponse("unauthorized"))
                call.respond(MeResponse(userId = user.id, email = user.email))
            }
            interactionRoutes("/posts", "/post-comments", postInteractions)
        }

        feedRoutes(feed)
        memorialRoutes(memorial, historyInteractions)
    }
}
