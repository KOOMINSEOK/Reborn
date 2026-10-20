package com.gentlelady.reborn.message

import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.message.data.MyGuestbookRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MyGuestbookRepositoryTest {

    private val json = headersOf(HttpHeaders.ContentType, "application/json")

    private fun entry(id: String) =
        """{"id":"$id","memorialId":"m1","memorialName":"홍길동","message":"msg $id","createdAt":"2026-10-20T00:00:00Z"}"""

    @Test
    fun followsCursorsUntilTheEnd() = runTest {
        val engine = MockEngine { req ->
            when (req.url.parameters["cursor"]) {
                null -> respond("""{"items":[${entry("a")},${entry("b")}],"nextCursor":"c1"}""", headers = json)
                "c1" -> respond("""{"items":[${entry("c")}]}""", headers = json)
                else -> error("unexpected ${req.url}")
            }
        }
        val api = RebornApi(
            HttpClient(engine) {
                install(ContentNegotiation) { json() }
                defaultRequest { url("http://api.test/") }
            },
            uploadClient = HttpClient(engine),
        )

        val books = MyGuestbookRepositoryImpl(api).getMyGuestbooks()

        assertEquals(listOf("a", "b", "c"), books.map { it.id })
        assertEquals("m1", books.first().memorialId)
        assertEquals("홍길동", books.first().deceasedName)
    }
}
