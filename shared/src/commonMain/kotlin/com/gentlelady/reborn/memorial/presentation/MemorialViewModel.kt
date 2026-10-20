// shared/src/commonMain/kotlin/com/gentlelady/reborn/memorial/presentation/MemorialViewModel.kt
package com.gentlelady.reborn.memorial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.data.MemorialMockData
import com.gentlelady.reborn.memorial.data.MemorialRepository
import com.gentlelady.reborn.memorial.domain.usecase.CreateGuestbookEntryUseCase
import com.gentlelady.reborn.memorial.domain.usecase.CreateHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MemorialViewModel(
    private val repository: MemorialRepository,
    private val createHistoryUseCase: CreateHistoryUseCase,
    private val createGuestbookEntryUseCase: CreateGuestbookEntryUseCase,
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

            // 전송 중엔 입력을 바꾸지 않는다 (보낸 글과 입력창이 어긋나지 않게)
            is MemorialIntent.UpdateGuestBookInput -> _state.update {
                if (it.isSubmittingGuestBook) it else it.copy(guestBookInputText = intent.text, guestBookError = null)
            }

            is MemorialIntent.SubmitGuestBook -> submitGuestBook()

            is MemorialIntent.LoadOlderGuestBook -> loadOlderGuestBook()

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

    private fun loadOlderGuestBook() {
        val current = _state.value
        val cursor = current.olderGuestBookCursor ?: return
        if (current.isLoadingOlderGuestBook || current.isLoading) return
        _state.update { it.copy(isLoadingOlderGuestBook = true) }
        viewModelScope.launch {
            val page = runCatching { repository.loadOlderGuestbook(current.memorialId, cursor) }.getOrNull()
            _state.update {
                if (it.memorialId != current.memorialId) return@update it.copy(isLoadingOlderGuestBook = false)
                // 실패하면 목록은 그대로, 다음에 맨 위에 닿을 때 다시 시도
                val (older, nextCursor) = page ?: return@update it.copy(isLoadingOlderGuestBook = false)
                val known = it.guestBookMessages.mapTo(HashSet()) { m -> m.id }
                it.copy(
                    guestBookMessages = older.filter { m -> m.id !in known } + it.guestBookMessages,
                    olderGuestBookCursor = nextCursor,
                    isLoadingOlderGuestBook = false,
                )
            }
        }
    }

    private fun submitGuestBook() {
        val current = _state.value
        if (current.guestBookInputText.isBlank() || current.isSubmittingGuestBook || current.isLoading) return
        _state.update { it.copy(isSubmittingGuestBook = true, guestBookError = null) }
        viewModelScope.launch {
            val result = createGuestbookEntryUseCase(current.memorialId, current.guestBookInputText)
            _state.update {
                // 그 사이 다른 추모관으로 바뀌었으면 결과를 섞지 않는다
                if (it.memorialId != current.memorialId) return@update it.copy(isSubmittingGuestBook = false)
                when (result) {
                    is CreateGuestbookEntryUseCase.Result.Posted -> it.copy(
                        guestBookMessages = it.guestBookMessages + result.item,
                        guestBookInputText = "",
                        isSubmittingGuestBook = false,
                    )
                    // 실패하면 입력은 그대로 둬서 다시 보낼 수 있게
                    is CreateGuestbookEntryUseCase.Result.Failed ->
                        it.copy(isSubmittingGuestBook = false, guestBookError = result.message)
                }
            }
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
            _state.update {
                // 그 사이 다른 추모관 로딩이 시작됐으면 늦게 온 결과는 버린다
                if (it.memorialId != id) return@update it
                // 서버 데이터만 갈아 끼운다 — 입력 중인 방명록·작성 폼·전송 중 상태는 보존 (재로딩이 전송을 풀지 않게)
                it.copy(
                    profile = loaded.profile,
                    historyItems = loaded.historyItems,
                    guestBookMessages = loaded.guestBookMessages,
                    olderGuestBookCursor = loaded.olderGuestBookCursor,
                    onlineWreathItems = loaded.onlineWreathItems,
                    selectedHistoryIndex = it.selectedHistoryIndex?.takeIf { i -> i < loaded.historyItems.size },
                    isLoading = false,
                )
            }
        }
    }
}
