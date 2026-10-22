package com.gentlelady.reborn.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.home.domain.usecase.GetHomeFeedUseCase
import com.gentlelady.reborn.interaction.presentation.LikeSync
import com.gentlelady.reborn.notification.GetUnreadNotificationCountUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomeFeedUseCase: GetHomeFeedUseCase,
    private val likeSync: LikeSync,
    private val getUnreadNotificationCount: GetUnreadNotificationCountUseCase,
    private val saveSync: com.gentlelady.reborn.interaction.presentation.SaveSync? = null,
    private val api: com.gentlelady.reborn.core.network.RebornApi? = null,
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
            is HomeIntent.ToggleSave -> toggleSave(intent.postId)
            is HomeIntent.ArchivePost -> archive(intent.postId)
            is HomeIntent.CommentCountChanged -> reduce(HomeResult.CommentCountChanged(intent.postId, intent.delta))
            is HomeIntent.HideAuthor -> reduce(HomeResult.AuthorHidden(intent.authorId))
            is HomeIntent.RefreshUnreadNotifications -> refreshUnread()
        }
    }

    private fun toggleLike(postId: String) {
        val post = _state.value.posts.firstOrNull { it.id == postId } ?: return
        likeSync.toggle(viewModelScope, InteractionTarget.POST, postId, post.liked, post.likes) { liked, likes ->
            reduce(HomeResult.LikeChanged(postId, liked, likes))
        }
    }

    private fun toggleSave(postId: String) {
        val post = _state.value.posts.firstOrNull { it.id == postId } ?: return
        saveSync?.toggle(viewModelScope, InteractionTarget.POST, postId, post.saved) { saved ->
            reduce(HomeResult.SaveChanged(postId, saved))
        }
    }

    /** 바로 빼고 서버에 보낸다. 실패하면 피드를 다시 받아 되돌린다 */
    private fun archive(postId: String) {
        val api = api ?: return
        reduce(HomeResult.PostRemoved(postId))
        viewModelScope.launch {
            if (runCatching { api.setArchived(postId, true) }.isFailure) loadFeed()
        }
    }

    private fun reduce(result: HomeResult) = _state.update { HomeReducer.reduce(it, result) }

    private var unreadJob: Job? = null

    /** 실패(로그인 안 함·서버 꺼짐)면 0 — 점을 띄우지 않는다. 마지막 요청만 반영. */
    private fun refreshUnread() {
        unreadJob?.cancel()
        unreadJob = viewModelScope.launch {
            val count = try {
                getUnreadNotificationCount()
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                0
            }
            reduce(HomeResult.UnreadNotifications(count))
        }
    }

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
