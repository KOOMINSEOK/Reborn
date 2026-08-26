package com.gentlelady.reborn.server.feed

import com.gentlelady.reborn.server.module
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

/** 인증 경계만 검증한다 (DB 없이). 쿼리 동작은 실 DB 통합 확인. */
class FeedRoutesTest {

    @Test
    fun feedRequiresAuth() = testApplication {
        application { module() }
        assertEquals(HttpStatusCode.Unauthorized, client.get("/feed").status)
    }

    @Test
    fun createPostRequiresAuth() = testApplication {
        application { module() }
        val response = client.post("/posts") {
            contentType(ContentType.Application.Json)
            setBody("""{"memorialId":"00000000-0000-0000-0000-000000000000"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun createMemorialRequiresAuth() = testApplication {
        application { module() }
        assertEquals(HttpStatusCode.Unauthorized, client.post("/memorials").status)
    }
}
