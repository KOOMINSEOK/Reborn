// shared/src/commonMain/kotlin/com/gentlelady/reborn/memorial/presentation/MemorialViewModel.kt
package com.gentlelady.reborn.memorial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.data.MemorialMockData
import com.gentlelady.reborn.memorial.data.MemorialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class MemorialViewModel(
    private val repository: MemorialRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MemorialMockData.memorialState)
    val state: StateFlow<MemorialState> = _state.asStateFlow()

    fun onIntent(intent: MemorialIntent) {
        when (intent) {
            is MemorialIntent.LoadMemorial -> load(intent.memorialId)

            is MemorialIntent.ClickBack -> {
                _state.update { current ->
                    when {
                        current.isWritingHistory -> current.copy(isWritingHistory = false)
                        current.selectedHistoryIndex != null -> current.copy(selectedHistoryIndex = null)
                        else -> current
                    }
                }
            }

            is MemorialIntent.SelectTab -> _state.update { it.copy(selectedTab = intent.tab) }

            is MemorialIntent.ClickHistoryImage ->
                _state.update { it.copy(selectedHistoryIndex = intent.index) }

            is MemorialIntent.UpdateGuestBookInput ->
                _state.update { it.copy(guestBookInputText = intent.text) }

            is MemorialIntent.SubmitGuestBook -> {
                _state.update { current ->
                    if (current.guestBookInputText.isBlank()) return@update current
                    // ponytail: 로컬 낙관 추가만. 서버 POST /memorials/{id}/guestbook 연동은 다음 브랜치.
                    val newItem = MemorialGuestBookItem(
                        id = "gb_${Clock.System.now().toEpochMilliseconds()}",
                        authorName = "나",
                        authorProfileUrl = null,
                        message = current.guestBookInputText,
                        timestamp = "방금 전",
                    )
                    current.copy(
                        guestBookMessages = current.guestBookMessages + newItem,
                        guestBookInputText = "",
                    )
                }
            }

            is MemorialIntent.ClickAddHistory ->
                _state.update { it.copy(isWritingHistory = true, historyWriteFormState = MemorialHistoryWriteFormState()) }

            is MemorialIntent.UpdateHistoryWriteCaption ->
                _state.update { it.copy(historyWriteFormState = it.historyWriteFormState.copy(caption = intent.caption)) }

            is MemorialIntent.UpdateHistoryWriteImage ->
                _state.update { it.copy(historyWriteFormState = it.historyWriteFormState.copy(imageBitmap = intent.imageBitmap)) }

            is MemorialIntent.ClickRemoveHistoryImage ->
                _state.update { it.copy(historyWriteFormState = it.historyWriteFormState.copy(imageBitmap = null)) }

            is MemorialIntent.ClickPostHistory -> {
                _state.update { current ->
                    val bitmap = current.historyWriteFormState.imageBitmap ?: return@update current
                    val newItem = MemorialHistoryItem(
                        id = "h_${Clock.System.now().toEpochMilliseconds()}",
                        imageBitmap = bitmap,
                        authorName = "나",
                        date = "방금 전",
                        caption = current.historyWriteFormState.caption,
                    )
                    current.copy(
                        historyItems = listOf(newItem) + current.historyItems,
                        isWritingHistory = false,
                        historyWriteFormState = MemorialHistoryWriteFormState(),
                    )
                }
            }

            is MemorialIntent.ClickCloseHistoryWrite -> _state.update { it.copy(isWritingHistory = false) }

            is MemorialIntent.AddOnlineWreathItem ->
                _state.update { it.copy(onlineWreathItems = listOf(intent.item) + it.onlineWreathItems) }

            else -> { /* 공유/음악/더보기/헌화/화환구매 등은 UI·NavGraph 에서 처리 */ }
        }
    }

    private fun load(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val loaded = repository.getMemorial(id)
            _state.update { loaded.copy(isLoading = false) }
        }
    }
}
