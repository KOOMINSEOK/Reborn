// shared/src/commonMain/kotlin/com/gentlelady/reborn/memorial/presentation/MemorialViewModel.kt
package com.gentlelady.reborn.memorial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.data.MemorialMockData
import com.gentlelady.reborn.memorial.data.MemorialRepository
import com.gentlelady.reborn.memorial.domain.usecase.CreateHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class MemorialViewModel(
    private val repository: MemorialRepository,
    private val createHistoryUseCase: CreateHistoryUseCase,
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

            // 게시 중에는 사진·본문을 바꾸지 않는다 (올라가는 내용과 화면이 어긋나지 않게)
            is MemorialIntent.UpdateHistoryWriteCaption -> updateForm { it.copy(caption = intent.caption, error = null) }

            is MemorialIntent.UpdateHistoryWriteImage -> updateForm { it.copy(image = intent.jpeg, error = null) }

            is MemorialIntent.ClickRemoveHistoryImage -> updateForm { it.copy(image = null) }

            is MemorialIntent.ClickPostHistory -> postHistory()

            is MemorialIntent.ClickCloseHistoryWrite -> _state.update { it.copy(isWritingHistory = false) }

            is MemorialIntent.AddOnlineWreathItem ->
                _state.update { it.copy(onlineWreathItems = listOf(intent.item) + it.onlineWreathItems) }

            else -> { /* 공유/음악/더보기/헌화/화환구매 등은 UI·NavGraph 에서 처리 */ }
        }
    }

    private fun updateForm(change: (MemorialHistoryWriteFormState) -> MemorialHistoryWriteFormState) {
        _state.update { s ->
            if (s.historyWriteFormState.isPosting) s else s.copy(historyWriteFormState = change(s.historyWriteFormState))
        }
    }

    private fun postHistory() {
        val current = _state.value
        val form = current.historyWriteFormState
        val image = form.image ?: return
        if (form.isPosting) return
        _state.update { it.copy(historyWriteFormState = form.copy(isPosting = true, error = null)) }
        viewModelScope.launch {
            when (val result = createHistoryUseCase(current.memorialId, form.caption, image)) {
                is CreateHistoryUseCase.Result.Posted -> _state.update {
                    it.copy(
                        historyItems = listOf(result.item) + it.historyItems,
                        isWritingHistory = false,
                        historyWriteFormState = MemorialHistoryWriteFormState(),
                    )
                }
                is CreateHistoryUseCase.Result.Failed -> _state.update {
                    it.copy(historyWriteFormState = it.historyWriteFormState.copy(isPosting = false, error = result.message))
                }
            }
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
