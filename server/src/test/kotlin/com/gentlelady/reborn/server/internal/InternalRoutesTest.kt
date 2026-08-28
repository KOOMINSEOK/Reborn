package com.gentlelady.reborn.server.internal

import com.gentlelady.reborn.server.module
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class InternalRoutesTest {

    @Test
    fun publishDueRejectsMissingSecret() = testApplication {
        application { module() }
        assertEquals(HttpStatusCode.Forbidden, client.post("/internal/publish-due").status)
    }

    @Test
    fun publishDueRejectsWrongSecret() = testApplication {
        application { module() }
        val response = client.post("/internal/publish-due") { header("X-Internal-Secret", "nope") }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }
}
