package com.gentlelady.reborn.notification

import com.gentlelady.reborn.core.network.RebornApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    private fun n(id: String, at: String, read: Boolean = false) = Notification(
        id, NotificationType.FOLLOW, "a", "이름", null, null, null, null, null, null, null, at, read,
    )

    private class FakeRepo : NotificationRepository {
        val pages = mutableListOf<CompletableDeferred<Pair<List<Notification>, String?>>>()
        val marked = mutableListOf<String>()
        override suspend fun notifications(cursor: String?) =
            CompletableDeferred<Pair<List<Notification>, String?>>().also { pages += it }.await()
        override suspend fun unreadCount() = 0
        override suspend fun markRead(upTo: String) { marked += upTo }
    }

    private fun vm(repo: FakeRepo) = NotificationsViewModel(GetNotificationsUseCase(repo), MarkNotificationsReadUseCase(repo))

    @Test
    fun marksReadUpToNewestSeenOnlyWhenSomethingIsUnread() = runTest(dispatcher) {
        val repo = FakeRepo()
        val vm = vm(repo)
        vm.onIntent(NotificationsIntent.Load)
        runCurrent()
        repo.pages[0].complete(listOf(n("2", "2026-10-21T10:00:00Z"), n("1", "2026-10-21T09:00:00Z", read = true)) to null)
        runCurrent()
        assertEquals(listOf("2026-10-21T10:00:00Z"), repo.marked) // 서버 시각 그대로 — 폰 시계를 쓰지 않는다
        assertEquals(listOf("2", "1"), vm.state.value.items.map { it.id })

        vm.onIntent(NotificationsIntent.Load) // 다시 열었는데 새 알림 없음
        runCurrent()
        repo.pages[1].complete(listOf(n("2", "2026-10-21T10:00:00Z", read = true)) to null)
        runCurrent()
        assertEquals(1, repo.marked.size) // 다시 읽음 요청 안 함
    }

    @Test
    fun lateResponseOfEarlierLoadIsIgnored() = runTest(dispatcher) {
        val repo = FakeRepo()
        val vm = vm(repo)
        vm.onIntent(NotificationsIntent.Load)
        runCurrent()
        vm.onIntent(NotificationsIntent.Load)
        runCurrent()
        repo.pages[1].complete(listOf(n("new", "2026-10-21T10:00:00Z")) to null)
        runCurrent()
        repo.pages[0].complete(listOf(n("old", "2026-10-21T09:00:00Z")) to null)
        runCurrent()
        assertEquals(listOf("new"), vm.state.value.items.map { it.id })
    }

    @Test
    fun relativeTimeInKorean() {
        val now = Instant.parse("2026-10-21T12:00:00Z")
        val seoul = TimeZone.of("Asia/Seoul")
        assertEquals("방금", relativeTime("2026-10-21T11:59:30Z", now, seoul))
        assertEquals("5분 전", relativeTime("2026-10-21T11:55:00Z", now, seoul))
        assertEquals("3시간 전", relativeTime("2026-10-21T09:00:00Z", now, seoul))
        assertEquals("2일 전", relativeTime("2026-10-19T12:00:00Z", now, seoul))
        assertEquals("10월 3일", relativeTime("2026-10-03T12:00:00Z", now, seoul))
        assertEquals("", relativeTime("이상한 값", now, seoul))
    }

    @Test
    fun unknownTypesFromNewerServerStayAsPlaceholders() = runTest {
        val json = headersOf(HttpHeaders.ContentType, "application/json")
        val body = """{"items":[
            {"id":"1","type":"follow","actorId":"a","actorName":"가","createdAt":"2026-10-21T10:00:00Z","read":false},
            {"id":"2","type":"mention","actorId":"b","actorName":"나","createdAt":"2026-10-21T09:00:00Z","read":false},
            {"id":"3","type":"guestbook","actorId":"c","actorName":"다","memorialId":"m","memorialName":"홍길동",
             "sourceId":"g","preview":"보고싶어","createdAt":"2026-10-21T08:00:00Z","read":true}],"nextCursor":null}"""
        val api = RebornApi(
            HttpClient(MockEngine { respond(body, headers = json) }) {
                install(ContentNegotiation) { json() }
                defaultRequest { url("http://api.test/") }
            },
            uploadClient = HttpClient(MockEngine { respond("") }),
        )
        val (items, next) = NotificationRepositoryImpl(api).notifications(null)
        // 버리면 한 페이지가 통째로 모르는 종류일 때 목록이 비어 더 불러오기·읽음 처리가 멈춘다
        assertEquals(listOf("1", "2", "3"), items.map { it.id })
        assertEquals(NotificationType.UNKNOWN, items[1].type)
        assertEquals(null, next)
        assertTrue(items[2].message.contains("홍길동님의 추모 페이지에 방명록을 남겼어요: 보고싶어"))
    }
}
