package com.gentlelady.reborn.server.feed

import com.gentlelady.reborn.server.module
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

/** 인증 경계만 검증 (DB 없이). */
class PostInteractionRoutesTest {

    private val id = "00000000-0000-0000-0000-000000000000"

    @Test
    fun postDetailRequiresAuth() = testApplication {
        application { module() }
        assertEquals(HttpStatusCode.Unauthorized, client.get("/posts/$id").status)
    }

    @Test
    fun likeRequiresAuth() = testApplication {
        application { module() }
        assertEquals(HttpStatusCode.Unauthorized, client.post("/posts/$id/like").status)
    }

    @Test
    fun listCommentsRequiresAuth() = testApplication {
        application { module() }
        assertEquals(HttpStatusCode.Unauthorized, client.get("/posts/$id/comments").status)
    }

    @Test
    fun deleteCommentRequiresAuth() = testApplication {
        application { module() }
        assertEquals(HttpStatusCode.Unauthorized, client.delete("/comments/$id").status)
    }
}
