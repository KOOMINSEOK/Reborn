package com.gentlelady.reborn.memorial

import com.gentlelady.reborn.core.auth.Session
import com.gentlelady.reborn.core.auth.SessionStore
import com.gentlelady.reborn.core.network.ImageUploader
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.memorial.data.MemorialRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MemorialGuestbookTest {

    private val json = headersOf(HttpHeaders.ContentType, "application/json")
    private val me = "user-me"

    private fun entry(id: String, author: String) =
        """{"id":"$id","memorialId":"m1","authorId":"$author","authorName":"n","message":"msg $id","createdAt":"2026-10-20T00:00:00Z"}"""

    private fun repo(): Pair<MemorialRepositoryImpl, MutableList<String>> {
        val posted = mutableListOf<String>()
        val engine = MockEngine { req ->
            val path = req.url.encodedPath
            when {
                req.method == HttpMethod.Post && path.endsWith("/memorials/m1/guestbook") -> {
                    posted += req.body.toByteArray().decodeToString()
                    respond(entry("new", me), headers = json)
                }
                path.endsWith("/memorials/m1") -> respond(
                    """{"id":"m1","creatorId":"c","name":"n","handle":"h","visibility":"public","followerCount":0}""",
                    headers = json,
                )
                path.endsWith("/memorials/m1/history") -> respond("""{"items":[]}""", headers = json)
                // 이전 페이지 (before=g1): 최신순으로 g0b, g0a
                path.endsWith("/memorials/m1/guestbook") && req.url.parameters["before"] == "g1" ->
                    respond("""{"items":[${entry("g0b", "other")},${entry("g0a", "other")}]}""", headers = json)
                // 서버는 최신순
                path.endsWith("/memorials/m1/guestbook") ->
                    respond("""{"items":[${entry("g2", "other")},${entry("g1", me)}]}""", headers = json)
                else -> error("unexpected ${req.method} ${req.url}")
            }
        }
        val api = RebornApi(
            HttpClient(engine) {
                install(ContentNegotiation) { json() }
                defaultRequest { url("http://api.test/"); contentType(ContentType.Application.Json) }
            },
            uploadClient = HttpClient(engine),
        )
        val session = SessionStore().apply { current = Session("T", "R", expiresAtEpochSeconds = Long.MAX_VALUE, userId = me) }
        return MemorialRepositoryImpl(api, ImageUploader(api), session) to posted
    }

    @Test
    fun loadsGuestbookOldestFirstAndMarksMyEntries() = runTest {
        val messages = repo().first.getMemorial("m1").guestBookMessages
        assertEquals(listOf("g1", "g2"), messages.map { it.id })
        assertEquals(listOf(true, false), messages.map { it.isMine })
    }

    @Test
    fun loadsOlderPageBeforeOldestInScreenOrder() = runTest {
        val (older, hasMore) = repo().first.loadOlderGuestbook("m1", beforeId = "g1")
        assertEquals(listOf("g0a", "g0b"), older.map { it.id })
        assertEquals(false, hasMore) // 30개 미만이면 더 없음
    }

    @Test
    fun createsGuestbookEntryOnServer() = runTest {
        val (repo, posted) = repo()
        val item = repo.createGuestbookEntry("m1", "보고싶어")
        assertEquals(listOf("""{"message":"보고싶어"}"""), posted)
        assertEquals("new", item.id)
        assertEquals(true, item.isMine)
    }
}
