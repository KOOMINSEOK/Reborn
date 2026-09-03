package com.gentlelady.reborn.server.profile

import com.gentlelady.reborn.server.module
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ProfileRoutesTest {

    private val id = "00000000-0000-0000-0000-000000000000"

    @Test
    fun protectedEndpointsRequireAuth() = testApplication {
        application { module() }

        assertEquals(HttpStatusCode.Unauthorized, client.get("/me/profile").status)
        assertEquals(HttpStatusCode.Unauthorized, client.patch("/me/profile").status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/users/$id").status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/users/$id/followers").status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/users/$id/following").status)
    }
}
