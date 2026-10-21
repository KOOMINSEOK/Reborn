package com.gentlelady.reborn.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.home.domain.usecase.GetHomeFeedUseCase
import com.gentlelady.reborn.interaction.presentation.LikeSync
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomeFeedUseCase: GetHomeFeedUseCase,
    private val likeSync: LikeSync,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadFeed()
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadFeed -> loadFeed()
            is HomeIntent.ClickMemorialIcon -> { /* 네비게이션은 UI 레이어에서 처리됨 */ }
            is HomeIntent.ToggleLike -> toggleLike(intent.postId)
            is HomeIntent.CommentCountChanged -> reduce(HomeResult.CommentCountChanged(intent.postId, intent.delta))
            is HomeIntent.HideAuthor -> reduce(HomeResult.AuthorHidden(intent.authorId))
        }
    }

    private fun toggleLike(postId: String) {
        val post = _state.value.posts.firstOrNull { it.id == postId } ?: return
        likeSync.toggle(viewModelScope, InteractionTarget.POST, postId, post.liked, post.likes) { liked, likes ->
            reduce(HomeResult.LikeChanged(postId, liked, likes))
        }
    }

    private fun reduce(result: HomeResult) = _state.update { HomeReducer.reduce(it, result) }

    private var feedJob: Job? = null
    private var feedGeneration = 0

    /** 마지막 요청만 반영한다 — 차단 전에 출발한 응답이 늦게 와서 차단한 사람 글을 되살리지 않게. */
    private fun loadFeed() {
        val gen = ++feedGeneration
        feedJob?.cancel()
        feedJob = viewModelScope.launch {
            _state.update { HomeReducer.reduce(it, HomeResult.Loading) }
            // 서버 우선, 실패 시 레포지토리가 mock 으로 폴백한다.
            val posts = getHomeFeedUseCase()
            if (gen == feedGeneration) _state.update { HomeReducer.reduce(it, HomeResult.FeedLoaded(posts)) }
        }
    }
}
