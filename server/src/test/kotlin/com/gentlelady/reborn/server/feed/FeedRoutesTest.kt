package com.gentlelady.reborn.server.feed

import com.gentlelady.reborn.server.module
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

/** 인증 경계만 검증 (DB 없이). 쿼리 동작은 실 DB 통합 확인. */
class FeedRoutesTest {

    private val id = "00000000-0000-0000-0000-000000000000"

    @Test
    fun protectedEndpointsRequireAuth() = testApplication {
        application { module() }

        assertEquals(HttpStatusCode.Unauthorized, client.get("/feed").status)
        assertEquals(HttpStatusCode.Unauthorized, client.post("/posts").status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/posts/$id").status)
        assertEquals(HttpStatusCode.Unauthorized, client.post("/users/$id/follow").status)
        assertEquals(HttpStatusCode.Unauthorized, client.delete("/users/$id/follow").status)
        assertEquals(HttpStatusCode.Unauthorized, client.post("/posts/$id/like").status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/posts/$id/comments").status)
    }
}
