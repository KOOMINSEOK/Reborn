package com.gentlelady.reborn.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.data.MockDataSource
import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.interaction.presentation.LikeSync
import com.gentlelady.reborn.search.data.SearchRepository
import com.gentlelady.reborn.search.domain.entity.MemorialSearchItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SEARCH_DEBOUNCE_MS = 300L

/** 검색 탭. 앱 전체에서 하나 — 계정이 바뀌면 다시 받는다. 둘러보기(로그인 안 함)면 목업. */
class SearchViewModel(
    private val repository: SearchRepository,
    private val currentUserId: CurrentUserIdUseCase,
    private val likeSync: LikeSync,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()
    private var searchJob: Job? = null
    private var feedJob: Job? = null

    init {
        viewModelScope.launch {
            currentUserId.changes.collect {
                _state.update { s -> s.copy(results = emptyList(), randomFeed = emptyList()) }
                loadRandomFeed()
                if (_state.value.isSearching) search(_state.value.query, _state.value.currentTab, debounce = false)
            }
        }
    }

    fun dispatch(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.UpdateQuery -> {
                _state.update { it.copy(query = intent.query) }
                search(intent.query, _state.value.currentTab, debounce = true)
            }
            // 정렬은 서버가 한다 (많은 사람 중 위쪽만 받으니까)
            is SearchIntent.ChangeTab -> {
                _state.update { it.copy(currentTab = intent.tab) }
                search(_state.value.query, intent.tab, debounce = false)
            }
            is SearchIntent.ExecuteSearch -> search(_state.value.query, _state.value.currentTab, debounce = false)
            is SearchIntent.Refresh -> if (!_state.value.isSearching) loadRandomFeed()
            is SearchIntent.ToggleLike -> toggleLike(intent.postId)
            is SearchIntent.CommentCountChanged -> updatePost(intent.postId) {
                it.copy(comments = (it.comments + intent.delta).coerceAtLeast(0))
            }
            is SearchIntent.ClickResultItem -> Unit // 네비게이션은 composeApp 그래프에서 처리
        }
    }

    /** 입력이 멈추면 찾는다. 늦게 온 옛 결과가 덮지 않게 이전 요청은 취소 */
    private fun search(query: String, tab: SearchTab, debounce: Boolean) {
        searchJob?.cancel()
        val q = query.trim()
        if (q.isEmpty()) {
            _state.update { it.copy(results = emptyList(), isLoading = false, error = null) }
            return
        }
        if (currentUserId() == null) {
            _state.update { it.copy(results = mockSearch(q, tab), error = null) }
            return
        }
        searchJob = viewModelScope.launch {
            if (debounce) delay(SEARCH_DEBOUNCE_MS)
            _state.update { it.copy(isLoading = true, error = null) }
            val results = runCatching { repository.search(q, tab) }.getOrNull()
            _state.update {
                if (results == null) it.copy(isLoading = false, error = "검색하지 못했어요. 잠시 후 다시 시도해 주세요.")
                else it.copy(isLoading = false, results = results)
            }
        }
    }

    /** 바로 바꿔 보여 주고 서버에 보낸다 (실패하면 서버가 확인한 값으로 되돌린다) */
    private fun toggleLike(postId: String) {
        if (currentUserId() == null) return // 둘러보기 — 목업 글
        val post = _state.value.randomFeed.firstOrNull { it.id == postId } ?: return
        likeSync.toggle(viewModelScope, InteractionTarget.POST, postId, post.liked, post.likes) { liked, likes ->
            updatePost(postId) { it.copy(liked = liked, likes = likes) }
        }
    }

    private fun updatePost(postId: String, change: (com.gentlelady.reborn.home.domain.model.HomePost) -> com.gentlelady.reborn.home.domain.model.HomePost) =
        _state.update { s -> s.copy(randomFeed = s.randomFeed.map { if (it.id == postId) change(it) else it }) }

    private fun loadRandomFeed() {
        feedJob?.cancel()
        if (currentUserId() == null) {
            _state.update { it.copy(randomFeed = MockDataSource.searchDefaultFeed()) }
            return
        }
        feedJob = viewModelScope.launch {
            val posts = runCatching { repository.randomPosts() }.getOrNull() ?: return@launch
            _state.update { it.copy(randomFeed = posts) }
        }
    }

    private fun mockSearch(q: String, tab: SearchTab): List<MemorialSearchItem> =
        MockDataSource.memorialSearchItems.filter { it.name.contains(q, ignoreCase = true) }.let { items ->
            when (tab) {
                SearchTab.LIKES -> items.sortedByDescending { it.likeValue }
                SearchTab.VIEWS -> items.sortedByDescending { it.viewValue }
                SearchTab.FLOWERS -> items.sortedByDescending { it.flowerValue }
            }
        }
}
