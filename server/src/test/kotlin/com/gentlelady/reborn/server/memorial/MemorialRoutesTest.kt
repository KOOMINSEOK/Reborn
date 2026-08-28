package com.gentlelady.reborn.server.memorial

import com.gentlelady.reborn.server.module
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class MemorialRoutesTest {

    private val id = "00000000-0000-0000-0000-000000000000"

    @Test
    fun protectedEndpointsRequireAuth() = testApplication {
        application { module() }

        assertEquals(HttpStatusCode.Unauthorized, client.post("/memorials").status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/memorials/$id").status)
        assertEquals(HttpStatusCode.Unauthorized, client.post("/memorials/$id/follow").status)
        assertEquals(HttpStatusCode.Unauthorized, client.post("/memorials/$id/history").status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/memorials/$id/history").status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/history/$id").status)
        assertEquals(HttpStatusCode.Unauthorized, client.post("/history/$id/like").status)
        assertEquals(HttpStatusCode.Unauthorized, client.post("/memorials/$id/guestbook").status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/memorials/$id/guestbook").status)
    }
}
