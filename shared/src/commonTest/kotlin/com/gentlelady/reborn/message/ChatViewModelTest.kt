package com.gentlelady.reborn.message

import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.core.auth.Session
import com.gentlelady.reborn.core.auth.SessionStore
import com.gentlelady.reborn.message.data.ChatLine
import com.gentlelady.reborn.message.data.ChatPeer
import com.gentlelady.reborn.message.data.ChatRepository
import com.gentlelady.reborn.message.presentation.ChatIntent
import com.gentlelady.reborn.message.presentation.ChatViewModel
import com.gentlelady.reborn.message.presentation.SuggestedUser
import com.gentlelady.reborn.message.presentation.chatDayLabel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    /** 대화창을 닫은 것처럼 — 새 메시지 확인이 끝없이 돌아 테스트가 안 끝나지 않게 */
    private fun closeAll() = made.forEach { it.viewModelScope.cancel() }

    /** 서버 흉내 — 메시지를 들고 있고, 보내기를 실패시킬 수 있다 */
    private class FakeChat : ChatRepository {
        val server = mutableListOf<ChatLine>()
        var sendFails = false
        var reads = 0
        private var seq = 0

        fun incoming(text: String, at: String) { server += ChatLine("s${seq++}", "other", text, at) }

        override suspend fun rooms() = emptyList<com.gentlelady.reborn.message.presentation.ChatRoomItem>()
        override suspend fun hide(conversationId: String) {}
        override suspend fun openWith(userId: String) = ChatPeer("c1", userId, "상대", null)
        override suspend fun room(conversationId: String) = ChatPeer(conversationId, "other", "상대", null)
        override suspend fun latest(conversationId: String, before: String?) =
            server.sortedByDescending { Instant.parse(it.createdAt) }.toList() to null
        override suspend fun newer(conversationId: String, after: String): List<ChatLine> {
            val at = Instant.parse(after.substringBeforeLast('_'))
            return server.filter { Instant.parse(it.createdAt) > at }.sortedBy { Instant.parse(it.createdAt) }
        }
        val byClientId = mutableMapOf<String, ChatLine>()
        override suspend fun send(conversationId: String, text: String, clientId: String): ChatLine {
            if (sendFails) error("down")
            return byClientId.getOrPut(clientId) { ChatLine("s${seq++}", "me", text, "2026-10-22T10:00:09Z").also { server += it } }
        }
        var readUpTo: String? = null
        override suspend fun markRead(conversationId: String, upTo: String) { reads++; readUpTo = upTo }
        override suspend fun search(query: String) = emptyList<SuggestedUser>()
        override suspend fun following(myId: String) = emptyList<SuggestedUser>()
    }

    private val made = mutableListOf<ChatViewModel>()

    private fun setup(): Pair<ChatViewModel, FakeChat> {
        val store = SessionStore().apply { current = Session("AT", "RT", 0, userId = "me") }
        val repo = FakeChat()
        return ChatViewModel(repo, CurrentUserIdUseCase(store)).also { made += it } to repo
    }

    @Test
    fun opensInTimeOrderEvenWithDifferentFractionDigits() = runTest(dispatcher) {
        try {
            val (vm, repo) = setup()
            // 글자 순서로는 .5Z 가 .25Z 보다 뒤지만 시각으로는 .25 가 먼저
            repo.incoming("둘째", "2026-10-22T10:00:01.5Z")
            repo.incoming("첫째", "2026-10-22T10:00:01.25Z")
            vm.onIntent(ChatIntent.Open("c1"))
            runCurrent()
            assertEquals(listOf("첫째", "둘째"), vm.state.value.messages.map { it.text })
            assertEquals(1, repo.reads) // 열면 읽음
            assertTrue(vm.state.value.messages.last().showAvatar) // 상대 묶음 마지막에만
            assertFalse(vm.state.value.messages.first().showAvatar)
        } finally {
            closeAll()
        }
    }

    @Test
    fun sendShowsPendingThenServerMessageOnceEvenIfPollFetchedItFirst() = runTest(dispatcher) {
        try {
            val (vm, repo) = setup()
            repo.incoming("안녕", "2026-10-22T10:00:00Z")
            vm.onIntent(ChatIntent.Open("c1"))
            runCurrent()
            vm.onIntent(ChatIntent.UpdateInput("반가워"))
            vm.onIntent(ChatIntent.Send)
            runCurrent()
            assertEquals(listOf("안녕", "반가워"), vm.state.value.messages.map { it.text })
            assertFalse(vm.state.value.messages.last().pending)
            assertEquals("", vm.state.value.input)
            advanceTimeBy(3_100) // 새 메시지 확인이 내가 보낸 걸 다시 받아 와도 한 번만
            runCurrent()
            assertEquals(listOf("안녕", "반가워"), vm.state.value.messages.map { it.text })
        } finally {
            closeAll()
        }
    }

    @Test
    fun failedSendStaysAsRetryableBubbleAndRetrySavesOnce() = runTest(dispatcher) {
        try {
            val (vm, repo) = setup()
            vm.onIntent(ChatIntent.Open("c1"))
            runCurrent()
            repo.sendFails = true
            vm.onIntent(ChatIntent.UpdateInput("보내질까"))
            vm.onIntent(ChatIntent.Send)
            runCurrent()
            val failed = vm.state.value.messages.single()
            assertTrue(failed.failed)
            assertEquals("보내질까", failed.text)
            repo.sendFails = false
            vm.onIntent(ChatIntent.Retry(failed.id))
            runCurrent()
            vm.onIntent(ChatIntent.Retry(failed.id)) // 두 번 눌러도
            runCurrent()
            assertEquals(listOf("보내질까"), vm.state.value.messages.map { it.text })
            assertFalse(vm.state.value.messages.single().failed)
            assertEquals(1, repo.server.size)
        } finally {
            closeAll()
        }
    }

    @Test
    fun pollingAddsNewMessagesAndMarksRead() = runTest(dispatcher) {
        try {
            val (vm, repo) = setup()
            repo.incoming("하나", "2026-10-22T10:00:00Z")
            vm.onIntent(ChatIntent.Open("c1"))
            runCurrent()
            repo.incoming("둘", "2026-10-22T10:00:05Z")
            advanceTimeBy(3_100)
            runCurrent()
            assertEquals(listOf("하나", "둘"), vm.state.value.messages.map { it.text })
            assertEquals(2, repo.reads)
            assertEquals("2026-10-22T10:00:05Z", repo.readUpTo) // 받은 것까지만 읽음
        } finally {
            closeAll()
        }
    }

    @Test
    fun dayLabels() {
        val now = Instant.parse("2026-10-22T03:00:00Z")
        val seoul = TimeZone.of("Asia/Seoul")
        assertEquals("오늘", chatDayLabel("2026-10-22T00:30:00Z", now, seoul))
        assertEquals("어제", chatDayLabel("2026-10-21T10:00:00Z", now, seoul))
        assertEquals("10월 3일", chatDayLabel("2026-10-03T03:00:00Z", now, seoul))
        assertEquals("2025년 12월 31일", chatDayLabel("2025-12-31T03:00:00Z", now, seoul))
        assertEquals("", chatDayLabel("", now, seoul))
    }

    @Test
    fun accountChangeClearsTheOpenChat() = runTest(dispatcher) {
        val store = SessionStore().apply { current = Session("AT", "RT", 0, userId = "me") }
        val repo = FakeChat().apply { incoming("비밀", "2026-10-22T10:00:00Z") }
        val vm = ChatViewModel(repo, CurrentUserIdUseCase(store)).also { made += it }
        try {
            vm.onIntent(ChatIntent.Open("c1"))
            runCurrent()
            assertEquals(1, vm.state.value.messages.size)
            store.current = Session("AT2", "RT2", 0, userId = "someone-else")
            runCurrent()
            assertTrue(vm.state.value.messages.isEmpty())
            vm.onIntent(ChatIntent.UpdateInput("새 계정으로 보내기"))
            vm.onIntent(ChatIntent.Send)
            runCurrent()
            assertTrue(repo.byClientId.isEmpty()) // 이전 대화방에 새 계정으로 보내지 않는다
        } finally {
            closeAll()
        }
    }
}
