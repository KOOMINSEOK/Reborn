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
                        // 게시 중엔 작성 화면을 못 닫는다 (닫았다 다시 열면 완료 결과가 새 초안을 덮어쓴다)
                        current.isWritingHistory && current.historyWriteFormState.isPosting -> current
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

            is MemorialIntent.ClickCloseHistoryWrite ->
                _state.update { if (it.historyWriteFormState.isPosting) it else it.copy(isWritingHistory = false) }

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
        // 불러오는 중이면 memorialId 가 아직 화면의 추모관과 다를 수 있다
        if (form.isPosting || current.isLoading) return
        _state.update { it.copy(historyWriteFormState = form.copy(isPosting = true, error = null)) }
        viewModelScope.launch {
            when (val result = createHistoryUseCase(current.memorialId, form.caption, image)) {
                is CreateHistoryUseCase.Result.Posted -> _state.update {
                    // 그 사이 다른 추모관으로 바뀌었으면 결과를 섞지 않는다 (글은 원래 추모관에 저장돼 있다)
                    if (it.memorialId != current.memorialId) return@update it
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
            // id 를 먼저 바꿔 둔다 → 로딩 중 게시가 이전 추모관으로 가지 않게
            _state.update { it.copy(memorialId = id, isLoading = true) }
            val loaded = repository.getMemorial(id)
            // 그 사이 다른 추모관 로딩이 시작됐으면 늦게 온 결과는 버린다
            _state.update { if (it.memorialId != id) it else loaded.copy(isLoading = false) }
        }
    }
}
