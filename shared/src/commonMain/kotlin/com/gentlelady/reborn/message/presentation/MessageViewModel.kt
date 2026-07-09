package com.gentlelady.reborn.message.presentation

import androidx.lifecycle.ViewModel // 🔔 androidx.lifecycle 버전 ViewModel 임포트
import com.gentlelady.reborn.data.MockDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Reborn 프로젝트 메시지/방명록 MVI ViewModel
 * 해결책: 명시적으로 ViewModel()을 상속받아 Koin 런타임 탐색 실패를 차단합니다.
 */
class MessageViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        MessageState(
            currentTab = MessageTab.MESSAGE,
            chatRooms = MockDataSource.messageChatRooms,
            guestBooks = MockDataSource.messageGuestBooks
        )
    )
    val state: StateFlow<MessageState> = _state.asStateFlow()

    fun handleIntent(intent: MessageIntent) {
        when (intent) {
            is MessageIntent.SelectTab -> {
                _state.update { it.copy(currentTab = intent.tab) }
            }
            is MessageIntent.UpdateSearchQuery -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }
            is MessageIntent.DeleteItem -> {
                if (intent.tab == MessageTab.MESSAGE) {
                    _state.update { current ->
                        current.copy(chatRooms = current.chatRooms.filterNot { it.id == intent.id })
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