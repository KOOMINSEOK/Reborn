package com.gentlelady.reborn.server.plugins

import com.gentlelady.reborn.server.chat.ChatRepository
import com.gentlelady.reborn.server.chat.chatRoutes
import com.gentlelady.reborn.server.feed.FeedRepository
import com.gentlelady.reborn.server.feed.feedRoutes
import com.gentlelady.reborn.server.interaction.InteractionRepo
import com.gentlelady.reborn.server.interaction.interactionRoutes
import com.gentlelady.reborn.server.internal.internalRoutes
import com.gentlelady.reborn.server.memorial.MemorialRepository
import com.gentlelady.reborn.server.memorial.memorialRoutes
import com.gentlelady.reborn.server.notification.NotificationRepository
import com.gentlelady.reborn.server.notification.notificationRoutes
import com.gentlelady.reborn.server.profile.ProfileRepository
import com.gentlelady.reborn.server.profile.profileRoutes
import com.gentlelady.reborn.server.search.SearchRepository
import com.gentlelady.reborn.server.search.searchRoutes
import com.gentlelady.reborn.server.signup.SignupRepository
import com.gentlelady.reborn.server.signup.naverRoutes
import com.gentlelady.reborn.server.signup.signupRoutes
import com.gentlelady.reborn.server.upload.uploadRoutes
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
    val profile = ProfileRepository()
    val search = SearchRepository()
    val notifications = NotificationRepository()
    val postInteractions = InteractionRepo("posts", "post_likes", "post_comments", "post_id")
    val historyInteractions = InteractionRepo("memorial_history", "history_likes", "history_comments", "history_id")

    routing {
        get("/health") {
            call.respond(HealthResponse(status = "ok", db = Db.status()))
        }

        // 시드용 더미 이미지. resources/static/seed/*.png → /static/seed/*.png
        staticResources("/static", "static")

        internalRoutes()

        authenticate(SUPABASE_AUTH) {
            get("/me") {
                val user = call.principal<UserPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ErrorResponse("unauthorized"))
                call.respond(MeResponse(userId = user.id, email = user.email))
            }
            interactionRoutes("/posts", "/post-comments", postInteractions) { viewer, id ->
                feed.getPost(viewer, id) != null
            }
        }

        feedRoutes(feed)
        memorialRoutes(memorial, historyInteractions, search)
        profileRoutes(profile, search)
        searchRoutes(search, feed)
        notificationRoutes(notifications)
        chatRoutes(ChatRepository())
        signupRoutes(SignupRepository())
        naverRoutes()
        uploadRoutes()
    }
}
