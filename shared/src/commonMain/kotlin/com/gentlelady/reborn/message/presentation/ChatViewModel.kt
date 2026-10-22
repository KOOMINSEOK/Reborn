package com.gentlelady.reborn.message.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.message.data.ChatLine
import com.gentlelady.reborn.message.data.ChatPeer
import com.gentlelady.reborn.message.data.ChatRepository
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

/** 새 메시지 확인 간격. ponytail: 대화창을 보는 동안만 물어본다 — 즉시 와야 하면 웹소켓·푸시로 */
private const val POLL_MS = 3_000L

data class ChatState(
    val peer: ChatPeer? = null,
    /** 오래된 것 → 최신 */
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val isLoading: Boolean = true,
    val olderCursor: String? = null,
    val isLoadingOlder: Boolean = false,
    /** 대화방을 열지 못함 (없는 사람·차단 관계·네트워크) */
    val loadError: String? = null,
    /** 잠깐 띄울 안내 */
    val message: String? = null,
)

sealed interface ChatIntent {
    /** 목록에서 연 대화방 */
    data class Open(val conversationId: String) : ChatIntent
    /** 프로필·검색에서 그 사람과 대화 시작 */
    data class OpenWith(val userId: String) : ChatIntent
    data class UpdateInput(val text: String) : ChatIntent
    object Send : ChatIntent
    object LoadOlder : ChatIntent
    object MessageShown : ChatIntent
}

/** 1:1 대화창. 화면(내비게이션 항목)마다 하나 — 화면을 나가면 새 메시지 확인도 멈춘다. */
class ChatViewModel(
    private val repository: ChatRepository,
    private val currentUserId: CurrentUserIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    /** 받은 메시지 (id 로 겹침 없이). 화면 목록은 여기서 시각순으로 만든다 */
    private val lines = mutableMapOf<String, ChatLine>()
    private val pending = mutableListOf<ChatMessage>()
    /** 새 메시지 확인과 보내기 결과가 같은 목록을 동시에 고치지 않게 */
    private val mutex = Mutex()
    private var openJob: Job? = null
    private var localSeq = 0

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.Open -> open { repository.room(intent.conversationId) }
            is ChatIntent.OpenWith -> open { repository.openWith(intent.userId) }
            is ChatIntent.UpdateInput -> _state.update { it.copy(input = intent.text.take(MAX_MESSAGE_LENGTH)) }
            is ChatIntent.Send -> send()
            is ChatIntent.LoadOlder -> loadOlder()
            is ChatIntent.MessageShown -> _state.update { it.copy(message = null) }
        }
    }

    private fun open(load: suspend () -> ChatPeer) {
        if (openJob != null) return // 같은 화면에서 다시 그려져도 한 번만
        openJob = viewModelScope.launch {
            val peer = try {
                load()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val text = if ((e as? ClientRequestException)?.response?.status?.value == 403) "메시지를 보낼 수 없는 사용자예요."
                else "대화를 불러오지 못했어요."
                _state.update { it.copy(isLoading = false, loadError = text) }
                openJob = null
                return@launch
            }
            _state.update { it.copy(peer = peer) }
            val first = runCatching { repository.latest(peer.conversationId) }.getOrNull()
            if (first == null) {
                _state.update { it.copy(isLoading = false, loadError = "대화를 불러오지 못했어요.") }
                openJob = null
                return@launch
            }
            mutex.withLock {
                first.first.asReversed().forEach { lines[it.id] = it }
                publish { it.copy(isLoading = false, olderCursor = first.second) }
            }
            runCatching { repository.markRead(peer.conversationId) }
            poll(peer)
        }
    }

    /** 대화창을 보는 동안 새 메시지를 확인. 상대 메시지가 오면 읽음으로 */
    private suspend fun poll(peer: ChatPeer) {
        while (true) {
            delay(POLL_MS)
            val after = mutex.withLock { lines.values.maxWithOrNull(compareBy({ Instant.parse(it.createdAt) }, { it.id }))?.cursor }
            val fresh = runCatching {
                if (after == null) repository.latest(peer.conversationId).first.asReversed() else repository.newer(peer.conversationId, after)
            }.getOrNull() ?: continue // 잠깐 끊김 — 다음에 다시
            if (fresh.isEmpty()) continue
            mutex.withLock {
                fresh.forEach { lines[it.id] = it }
                publish()
            }
            if (fresh.any { it.senderId != currentUserId() }) runCatching { repository.markRead(peer.conversationId) }
        }
    }

    private fun send() {
        val peer = _state.value.peer ?: return
        val text = _state.value.input.trim()
        if (text.isEmpty()) return
        val local = ChatMessage(id = "local-${localSeq++}", text = text, isMine = true, pending = true)
        viewModelScope.launch {
            mutex.withLock {
                pending += local
                publish { it.copy(input = "") }
            }
            val sent = try {
                repository.send(peer.conversationId, text)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mutex.withLock {
                    pending -= local
                    // 입력 중이던 글은 지우지 않는다 — 비어 있을 때만 되돌린다
                    publish { it.copy(input = it.input.ifEmpty { text }, message = "메시지를 보내지 못했어요. 다시 시도해 주세요.") }
                }
                return@launch
            }
            mutex.withLock {
                pending -= local
                lines[sent.id] = sent // 새 메시지 확인이 먼저 받아 왔어도 id 가 같아 한 번만
                publish()
            }
        }
    }

    private fun loadOlder() {
        val s = _state.value
        val peer = s.peer ?: return
        val cursor = s.olderCursor ?: return
        if (s.isLoadingOlder) return
        _state.update { it.copy(isLoadingOlder = true) }
        viewModelScope.launch {
            val page = runCatching { repository.latest(peer.conversationId, before = cursor) }.getOrNull()
            mutex.withLock {
                if (page == null) {
                    _state.update { it.copy(isLoadingOlder = false) }
                    return@withLock
                }
                page.first.forEach { lines[it.id] = it } // 순서는 publish 가 시각으로 맞춘다
                publish { it.copy(isLoadingOlder = false, olderCursor = page.second) }
            }
        }
    }

    /** mutex 안에서만 */
    private fun publish(change: (ChatState) -> ChatState = { it }) {
        val me = currentUserId()
        // 시각 글자는 소수점 자릿수가 달라 글자 순서로 비교하면 틀린다 → Instant 로
        val shown = lines.values.sortedWith(compareBy({ Instant.parse(it.createdAt) }, { it.id })).map {
            ChatMessage(id = it.id, text = it.text, isMine = it.senderId == me, createdAt = it.createdAt)
        } + pending
        _state.update { change(it).copy(messages = shown.withAvatars()) }
    }
}

/** 상대 말풍선 묶음의 마지막에만 아바타 */
private fun List<ChatMessage>.withAvatars() = mapIndexed { i, m ->
    m.copy(showAvatar = !m.isMine && getOrNull(i + 1)?.isMine != false)
}

const val MAX_MESSAGE_LENGTH = 2000

/** 대화 날짜 구분선: "오늘" · "어제" · "10월 3일" · 해가 다르면 "2025년 10월 3일". 시각이 없으면(보내는 중) 빈 값 */
fun chatDayLabel(
    createdAt: String,
    now: Instant = kotlinx.datetime.Clock.System.now(),
    zone: kotlinx.datetime.TimeZone = kotlinx.datetime.TimeZone.currentSystemDefault(),
): String {
    val day = runCatching { Instant.parse(createdAt) }.getOrNull()?.toLocalDateTime(zone)?.date ?: return ""
    val today = now.toLocalDateTime(zone).date
    return when {
        day == today -> "오늘"
        day == today.minus(1, DateTimeUnit.DAY) -> "어제"
        day.year == today.year -> "${day.monthNumber}월 ${day.dayOfMonth}일"
        else -> "${day.year}년 ${day.monthNumber}월 ${day.dayOfMonth}일"
    }
}
