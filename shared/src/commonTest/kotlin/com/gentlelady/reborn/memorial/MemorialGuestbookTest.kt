package com.gentlelady.reborn.memorial

import com.gentlelady.reborn.core.auth.Session
import com.gentlelady.reborn.core.auth.SessionStore
import com.gentlelady.reborn.core.network.ImageUploader
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.memorial.data.MemorialRepositoryImpl
import com.gentlelady.reborn.memorial.presentation.MemorialTab
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
                // 특정 글 주변 (around=g5): 최신순 g6, g5, g4 + 양방향 커서
                path.endsWith("/memorials/m1/guestbook") && req.url.parameters["around"] == "g5" -> respond(
                    """{"items":[${entry("g6", "other")},${entry("g5", me)},${entry("g4", "other")}],
                    "nextCursor":"old1","newerCursor":"new1"}""",
                    headers = json,
                )
                // 이후 페이지 (after=new1): 최신순 g8, g7, 더 없음
                path.endsWith("/memorials/m1/guestbook") && req.url.parameters["after"] == "new1" ->
                    respond("""{"items":[${entry("g8", "other")},${entry("g7", "other")}]}""", headers = json)
                // 이전 페이지 (cursor=c1): 최신순으로 g0b, g0a, 다음 커서 c2
                path.endsWith("/memorials/m1/guestbook") && req.url.parameters["cursor"] == "c1" ->
                    respond("""{"items":[${entry("g0b", "other")},${entry("g0a", "other")}],"nextCursor":"c2"}""", headers = json)
                // 서버는 최신순
                path.endsWith("/memorials/m1/guestbook") ->
                    respond("""{"items":[${entry("g2", "other")},${entry("g1", me)}],"nextCursor":"c1"}""", headers = json)
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
        val state = repo().first.getMemorial("m1")
        val messages = state.guestBookMessages
        assertEquals("c1", state.olderGuestBookCursor)
        assertEquals(listOf("g1", "g2"), messages.map { it.id })
        assertEquals(listOf(true, false), messages.map { it.isMine })
    }

    @Test
    fun loadsOlderPageByCursorInScreenOrder() = runTest {
        val (older, next) = repo().first.loadOlderGuestbook("m1", cursor = "c1")
        assertEquals(listOf("g0a", "g0b"), older.map { it.id })
        assertEquals("c2", next)
    }

    @Test
    fun opensGuestbookAroundFocusedEntry() = runTest {
        val state = repo().first.getMemorial("m1", focusGuestBookId = "g5")
        assertEquals(listOf("g4", "g5", "g6"), state.guestBookMessages.map { it.id })
        assertEquals("g5", state.guestBookFocusId)
        assertEquals(MemorialTab.GUESTBOOK, state.selectedTab)
        assertEquals("old1", state.olderGuestBookCursor)
        assertEquals("new1", state.newerGuestBookCursor)
    }

    @Test
    fun loadsNewerPageInScreenOrder() = runTest {
        val (newer, next) = repo().first.loadNewerGuestbook("m1", cursor = "new1")
        assertEquals(listOf("g7", "g8"), newer.map { it.id })
        assertEquals(null, next)
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
