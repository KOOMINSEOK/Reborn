package com.gentlelady.reborn.home

import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.data.HomeMockData
import com.gentlelady.reborn.home.data.repository.HomeRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeRepositoryImplTest {

    private fun api(handler: MockEngine): RebornApi = RebornApi(
        HttpClient(handler) {
            install(ContentNegotiation) { json() }
        },
    )

    @Test
    fun fallsBackToMockWhenServerErrors() = runTest {
        val repo = HomeRepositoryImpl(api(MockEngine { respond("boom", HttpStatusCode.InternalServerError) }))

        assertEquals(HomeMockData.feed, repo.getHomeFeed())
    }

    @Test
    fun returnsServerFeedWhenAvailable() = runTest {
        val json = """
            {"items":[{"id":"s1","authorId":"a","authorName":"서버유저","authorHandle":"h",
            "caption":"서버 글","isPosthumous":false,"status":"published",
            "likeCount":5,"commentCount":2,"liked":false,"createdAt":"2026-08-27T10:00:00Z"}],"nextOffset":null}
        """.trimIndent()
        val repo = HomeRepositoryImpl(
            api(MockEngine { respond(json, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json")) }),
        )

        val feed = repo.getHomeFeed()

        assertEquals(1, feed.size)
        assertEquals("서버 글", feed.first().caption)
        assertEquals(5, feed.first().likes)
        assertEquals("2026-08-27", feed.first().postedAt)
    }
}
