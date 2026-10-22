// shared/src/commonMain/kotlin/com/gentlelady/reborn/memorial/presentation/MemorialViewModel.kt
package com.gentlelady.reborn.memorial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.data.MemorialMockData
import com.gentlelady.reborn.interaction.presentation.LikeSync
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
    private val likeSync: LikeSync,
    private val saveSync: com.gentlelady.reborn.interaction.presentation.SaveSync,
) : ViewModel() {

    private val _state = MutableStateFlow(MemorialMockData.memorialState)
    val state: StateFlow<MemorialState> = _state.asStateFlow()

    fun onIntent(intent: MemorialIntent) {
        when (intent) {
            is MemorialIntent.LoadMemorial -> load(intent.memorialId, intent.focusGuestBookId, intent.focusHistoryId)

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

            is MemorialIntent.ToggleHistoryLike -> toggleHistoryLike(intent.historyId)
            is MemorialIntent.ToggleHistorySave -> {
                val item = _state.value.historyItems.firstOrNull { it.id == intent.historyId }
                if (item != null) saveSync.toggle(viewModelScope, InteractionTarget.HISTORY, item.id, item.saved) { saved ->
                    updateHistory(item.id) { it.copy(saved = saved) }
                }
            }

            is MemorialIntent.HistoryCommentCountChanged -> updateHistory(intent.historyId) {
                it.copy(comments = (it.comments + intent.delta).coerceAtLeast(0))
            }

            is MemorialIntent.ClickHistoryImage ->
                _state.update { it.copy(selectedHistoryIndex = intent.index) }

            // 전송 중엔 입력을 바꾸지 않는다 (보낸 글과 입력창이 어긋나지 않게)
            is MemorialIntent.UpdateGuestBookInput -> _state.update {
                if (it.isSubmittingGuestBook) it else it.copy(guestBookInputText = intent.text, guestBookError = null)
            }

            is MemorialIntent.SubmitGuestBook -> submitGuestBook()

            is MemorialIntent.LoadOlderGuestBook -> loadOlderGuestBook()

            is MemorialIntent.LoadNewerGuestBook -> loadNewerGuestBook()

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

    private fun toggleHistoryLike(historyId: String) {
        val item = _state.value.historyItems.firstOrNull { it.id == historyId } ?: return
        likeSync.toggle(viewModelScope, InteractionTarget.HISTORY, historyId, item.liked, item.likes) { liked, likes ->
            updateHistory(historyId) { it.copy(liked = liked, likes = likes) }
        }
    }

    private fun updateHistory(id: String, change: (MemorialHistoryItem) -> MemorialHistoryItem) =
        _state.update { s -> s.copy(historyItems = s.historyItems.map { if (it.id == id) change(it) else it }) }

    private fun loadNewerGuestBook() {
        val current = _state.value
        val cursor = current.newerGuestBookCursor ?: return
        if (current.isLoadingNewerGuestBook || current.isLoading) return
        _state.update { it.copy(isLoadingNewerGuestBook = true) }
        viewModelScope.launch {
            val page = runCatching { repository.loadNewerGuestbook(current.memorialId, cursor) }.getOrNull()
            _state.update {
                // 그 사이 다른 추모관이 됐거나 목록이 통째로 바뀌었으면(최신으로 교체 등) 이 응답은 버린다
                if (it.memorialId != current.memorialId || it.guestBookWindow != current.guestBookWindow) {
                    return@update it.copy(isLoadingNewerGuestBook = false)
                }
                // 실패하면 목록은 그대로, 다음에 맨 아래에 닿을 때 다시 시도
                val (newer, nextNewer) = page ?: return@update it.copy(isLoadingNewerGuestBook = false)
                val known = it.guestBookMessages.mapTo(HashSet()) { m -> m.id }
                it.copy(
                    guestBookMessages = it.guestBookMessages + newer.filter { m -> m.id !in known },
                    newerGuestBookCursor = nextNewer,
                    isLoadingNewerGuestBook = false,
                )
            }
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
                if (it.memorialId != current.memorialId || it.guestBookWindow != current.guestBookWindow) {
                    return@update it.copy(isLoadingOlderGuestBook = false)
                }
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
            // 이후 글이 아직 남은 중간 지점을 보고 있었다면, 새 글을 붙일 자리(최신)가 화면에 없다 → 최신 페이지를 다시 받는다
            val latest = if (result is CreateGuestbookEntryUseCase.Result.Posted && current.newerGuestBookCursor != null) {
                runCatching { repository.latestGuestbook(current.memorialId) }.getOrNull()
            } else {
                null
            }
            _state.update {
                // 그 사이 다른 추모관으로 바뀌었으면 결과를 섞지 않는다
                if (it.memorialId != current.memorialId) return@update it.copy(isSubmittingGuestBook = false)
                when (result) {
                    is CreateGuestbookEntryUseCase.Result.Posted -> it.copy(
                        guestBookMessages = latest?.first
                            ?: (it.guestBookMessages + result.item).distinctBy { m -> m.id },
                        olderGuestBookCursor = latest?.second ?: it.olderGuestBookCursor,
                        newerGuestBookCursor = if (latest != null) null else it.newerGuestBookCursor,
                        guestBookInputText = "",
                        isSubmittingGuestBook = false,
                        guestBookFocusId = null,
                        guestBookScrollToken = it.guestBookScrollToken + 1, // 맨 아래(방금 쓴 글)로
                        // 최신 페이지로 통째로 바꿨으면 진행 중이던 페이지 응답은 무효
                        guestBookWindow = if (latest != null) it.guestBookWindow + 1 else it.guestBookWindow,
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

    private fun load(id: String, focusGuestBookId: String?, focusHistoryId: String? = null) {
        viewModelScope.launch {
            // id 를 먼저 바꿔 둔다 → 로딩 중 게시가 이전 추모관으로 가지 않게
            _state.update { it.copy(memorialId = id, isLoading = true) }
            val loaded = repository.getMemorial(id, focusGuestBookId)
            _state.update {
                // 그 사이 다른 추모관 로딩이 시작됐으면 늦게 온 결과는 버린다
                if (it.memorialId != id) return@update it
                // 서버 데이터만 갈아 끼운다 — 입력 중인 방명록·작성 폼·전송 중 상태는 보존 (재로딩이 전송을 풀지 않게)
                it.copy(
                    profile = loaded.profile,
                    historyItems = loaded.historyItems,
                    guestBookMessages = loaded.guestBookMessages,
                    olderGuestBookCursor = loaded.olderGuestBookCursor,
                    newerGuestBookCursor = loaded.newerGuestBookCursor,
                    guestBookFocusId = loaded.guestBookFocusId,
                    guestBookScrollToken = it.guestBookScrollToken + 1,
                    guestBookWindow = it.guestBookWindow + 1,
                    // 특정 방명록으로 들어왔으면 방명록 탭을 연다
                    selectedTab = if (loaded.guestBookFocusId != null) MemorialTab.GUESTBOOK else it.selectedTab,
                    onlineWreathItems = loaded.onlineWreathItems,
                    // 저장한 추억 글에서 들어왔으면 그 글을 연다 (첫 페이지에 없으면 추모 페이지만)
                    selectedHistoryIndex = focusHistoryId?.let { f -> loaded.historyItems.indexOfFirst { h -> h.id == f }.takeIf { i -> i >= 0 } }
                        ?: it.selectedHistoryIndex?.takeIf { i -> i < loaded.historyItems.size },
                    isLoading = false,
                )
            }
        }
    }
}
