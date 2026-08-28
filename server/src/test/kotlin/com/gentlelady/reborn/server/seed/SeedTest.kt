package com.gentlelady.reborn.server.seed

import com.gentlelady.reborn.server.module
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SeedTest {

    @Test
    fun servesBundledSeedImage() = testApplication {
        application { module() }

        val response = client.get("/static/seed/img_post_dummy1.png")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.body<ByteArray>().isNotEmpty())
    }

    @Test
    fun seedDataHasNoNameCollisions() {
        val names = SeedData.users.map { it.name }
        assertEquals(names.size, names.toSet().size)
        SeedData.posts.forEach { assertTrue(it.authorName in SeedData.userIdByName) }
        SeedData.history.forEach { assertTrue(it.authorName in SeedData.userIdByName) }
    }
}
