package com.gentlelady.reborn.management.saved.presentation

import androidx.lifecycle.ViewModel
import com.gentlelady.reborn.data.SavedMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SavedViewModel : ViewModel() {

    private val _state = MutableStateFlow(SavedState())
    val state: StateFlow<SavedState> = _state.asStateFlow()

    init {
        handleIntent(SavedIntent.LoadSaved)
    }

    fun handleIntent(intent: SavedIntent) {
        when (intent) {
            is SavedIntent.LoadSaved -> loadMockData()
            is SavedIntent.ClickBack -> { /* 상위 네비게이션에서 처리 */ }
            is SavedIntent.ClickTab -> _state.update { it.copy(selectedTab = intent.tab) }
            is SavedIntent.ClickPost -> { /* TODO: 저장된 게시물 상세 화면 연동 */ }
            is SavedIntent.ClickHistoryItem -> { /* TODO: 저장된 히스토리 상세 화면 연동 */ }
            is SavedIntent.ClickGuestBookEntry -> { /* TODO: 저장된 방명록이 남겨진 메모리얼로 이동 */ }
        }
    }

    private fun loadMockData() {
        _state.update {
            it.copy(
                posts = SavedMockData.savedPosts,
                history = SavedMockData.savedHistory,
                guestBookEntries = SavedMockData.savedGuestBookEntries,
                isLoading = false
            )
        }
    }
}
