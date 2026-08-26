package com.gentlelady.reborn.server

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class SecurityTest {

    @Test
    fun meWithoutTokenIsUnauthorized() = testApplication {
        application { module() }

        assertEquals(HttpStatusCode.Unauthorized, client.get("/me").status)
    }

    @Test
    fun meWithGarbageTokenIsUnauthorized() = testApplication {
        application { module() }

        val response = client.get("/me") {
            header(HttpHeaders.Authorization, "Bearer not-a-real-jwt")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}
