package com.gentlelady.reborn.server

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HealthTest {

    /** DB 미설정이어도 서버는 뜨고 /health 는 200 을 준다 — 스캐폴드의 핵심 계약. */
    @Test
    fun healthRespondsOkWithoutDatabase() = testApplication {
        application { module() }

        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"status\":\"ok\""), "unexpected body: $body")
        assertTrue(body.contains("\"db\":\"not_configured\""), "unexpected body: $body")
    }
}
