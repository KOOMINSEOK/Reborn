package com.gentlelady.reborn.management.saved.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.toAbsoluteServerUrl
import com.gentlelady.reborn.data.SavedMockData
import com.gentlelady.reborn.management.saved.domain.model.SavedGridItem
import com.gentlelady.reborn.management.saved.domain.model.SavedGuestBookItem
import com.gentlelady.reborn.message.data.MyGuestbookRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 저장됨 (관리 > 저장). 앱 전체에서 하나 — 화면에 들어올 때마다 새로 받고, 계정이 바뀌면 비운다.
 * 방명록 탭 = 내가 남긴 방명록 (따로 저장하지 않는다 — 메시지 탭과 같은 목록).
 * 둘러보기(로그인 안 함)면 목업.
 */
class SavedViewModel(
    private val api: RebornApi,
    private val myGuestbooks: MyGuestbookRepository,
    private val currentUserId: CurrentUserIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SavedState())
    val state: StateFlow<SavedState> = _state.asStateFlow()
    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            currentUserId.changes.collect {
                _state.update { s -> SavedState(selectedTab = s.selectedTab) }
                load()
            }
        }
    }

    fun handleIntent(intent: SavedIntent) {
        when (intent) {
            is SavedIntent.LoadSaved -> load()
            is SavedIntent.ClickBack -> Unit // 상위 네비게이션에서 처리
            is SavedIntent.ClickTab -> _state.update { it.copy(selectedTab = intent.tab) }
            // 이동은 composeApp 그래프에서
            is SavedIntent.ClickPost, is SavedIntent.ClickHistoryItem, is SavedIntent.ClickGuestBookEntry -> Unit
        }
    }

    /** 세 목록을 같이 받는다. 늦게 온 옛 응답이 덮지 않게 이전 요청은 취소. 실패하면 지금 목록 유지 + 안내 */
    private fun load() {
        loadJob?.cancel()
        if (currentUserId() == null) {
            _state.update {
                it.copy(
                    posts = SavedMockData.savedPosts,
                    history = SavedMockData.savedHistory,
                    guestBookEntries = SavedMockData.savedGuestBookEntries,
                    isLoading = false,
                    error = null,
                )
            }
            return
        }
        loadJob = viewModelScope.launch {
            val result = runCatching {
                coroutineScope {
                    val posts = async { api.allPages { o -> api.savedPosts(o).let { it.items to it.nextOffset } } }
                    val history = async { api.allPages { o -> api.savedHistory(o).let { it.items to it.nextOffset } } }
                    val books = async { myGuestbooks.getMyGuestbooks() }
                    Triple(posts.await(), history.await(), books.await())
                }
            }
            val (posts, history, books) = result.getOrNull() ?: run {
                _state.update { it.copy(isLoading = false, error = "저장한 글을 불러오지 못했어요.") }
                return@launch
            }
            _state.update {
                it.copy(
                    posts = posts.map { p ->
                        SavedGridItem(p.id, imageUrl = p.imageUrls.firstOrNull().toAbsoluteServerUrl(), ownerId = p.authorId)
                    },
                    history = history.map { h ->
                        SavedGridItem(h.id, imageUrl = h.imageUrls.firstOrNull().toAbsoluteServerUrl(), ownerId = h.memorialId)
                    },
                    guestBookEntries = books.map { b ->
                        SavedGuestBookItem(
                            id = b.id,
                            memorialName = "故 ${b.deceasedName} 님의 메모리얼",
                            message = b.recentContent,
                            dateLabel = b.relativeTime,
                            authorAvatar = null,
                            memorialId = b.memorialId,
                        )
                    },
                    isLoading = false,
                    error = null,
                )
            }
        }
    }
}
