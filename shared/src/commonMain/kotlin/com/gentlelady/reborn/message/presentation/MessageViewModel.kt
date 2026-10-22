package com.gentlelady.reborn.message.presentation

import androidx.lifecycle.ViewModel // 🔔 androidx.lifecycle 버전 ViewModel 임포트
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.data.MockDataSource
import com.gentlelady.reborn.message.data.ChatRepository
import com.gentlelady.reborn.message.data.MyGuestbookRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Reborn 프로젝트 메시지/방명록 MVI ViewModel
 * 해결책: 명시적으로 ViewModel()을 상속받아 Koin 런타임 탐색 실패를 차단합니다.
 */
class MessageViewModel(
    private val myGuestbookRepository: MyGuestbookRepository,
    private val chatRepository: ChatRepository,
    private val currentUserId: CurrentUserIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(
        MessageState(
            currentTab = MessageTab.MESSAGE,
            chatRooms = MockDataSource.messageChatRooms,
            guestBooks = MockDataSource.messageGuestBooks
        )
    )
    val state: StateFlow<MessageState> = _state.asStateFlow()

    init {
        loadMyGuestBooks()
        // 앱 전체에서 하나 — 계정이 바뀌면 이전 사람 대화방을 버리고 다시 (둘러보기면 목업)
        viewModelScope.launch {
            currentUserId.changes.collect { userId ->
                _state.update { it.copy(chatRooms = if (userId == null) MockDataSource.messageChatRooms else emptyList()) }
                if (userId != null) loadRooms()
            }
        }
    }

    private var myGuestBooksJob: Job? = null
    private var roomsJob: Job? = null

    /** 늦게 온 옛 응답이 덮지 않게 이전 요청은 취소. 실패하면 지금 목록 유지 */
    private fun loadRooms() {
        if (currentUserId() == null) return
        roomsJob?.cancel()
        roomsJob = viewModelScope.launch {
            val rooms = runCatching { chatRepository.rooms() }.getOrNull() ?: return@launch
            _state.update { it.copy(chatRooms = rooms) }
        }
    }

    /** 내가 남긴 방명록을 서버에서 (방명록 탭을 열 때마다 새로 — 다른 화면에서 쓴 글 반영). 늦게 온 옛 응답이 덮지 않게 이전 요청은 취소 */
    private fun loadMyGuestBooks() {
        myGuestBooksJob?.cancel()
        myGuestBooksJob = viewModelScope.launch {
            val books = myGuestbookRepository.getMyGuestbooks()
            _state.update { it.copy(guestBooks = books) }
        }
    }

    fun handleIntent(intent: MessageIntent) {
        when (intent) {
            is MessageIntent.SelectTab -> {
                _state.update { it.copy(currentTab = intent.tab) }
                if (intent.tab == MessageTab.GUEST_BOOK) loadMyGuestBooks()
            }
            is MessageIntent.UpdateSearchQuery -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }
            is MessageIntent.Refresh -> {
                loadRooms()
                if (_state.value.currentTab == MessageTab.GUEST_BOOK) loadMyGuestBooks()
            }
            is MessageIntent.DeleteItem -> {
                if (intent.tab == MessageTab.MESSAGE) {
                    _state.update { current ->
                        current.copy(chatRooms = current.chatRooms.filterNot { it.id == intent.id })
                    }
                    // 내 목록에서만 지운다 (상대에겐 그대로). 실패하면 목록을 다시 받아 되돌린다
                    if (currentUserId() != null) viewModelScope.launch {
                        if (runCatching { chatRepository.hide(intent.id) }.isFailure) loadRooms()
                    }
                } else {
                    _state.update { current ->
                        current.copy(guestBooks = current.guestBooks.filterNot { it.id == intent.id })
                    }
                }
            }
            else -> {}
        }
    }
}