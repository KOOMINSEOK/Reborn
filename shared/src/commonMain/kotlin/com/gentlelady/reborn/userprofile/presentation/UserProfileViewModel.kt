package com.gentlelady.reborn.userprofile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.interaction.presentation.LikeSync
import com.gentlelady.reborn.interaction.presentation.ToggleSync
import com.gentlelady.reborn.userprofile.domain.FollowRejectedException
import com.gentlelady.reborn.userprofile.domain.GetUserPostsUseCase
import com.gentlelady.reborn.userprofile.domain.GetUserProfileUseCase
import com.gentlelady.reborn.userprofile.domain.SetBlockUseCase
import com.gentlelady.reborn.userprofile.domain.SetFollowUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 다른 사람 프로필 (헤더 + 그 사람 글 목록 + 팔로우). 화면(내비게이션 항목)마다 하나.
 * 다른 사람으로 [UserProfileIntent.Load] 하면 처음부터, 늦은 응답은 버린다 (generation).
 */
class UserProfileViewModel(
    private val getProfile: GetUserProfileUseCase,
    private val getPosts: GetUserPostsUseCase,
    setFollow: SetFollowUseCase,
    private val likeSync: LikeSync,
    private val setBlock: SetBlockUseCase,
    private val saveSync: com.gentlelady.reborn.interaction.presentation.SaveSync? = null,
    private val api: com.gentlelady.reborn.core.network.RebornApi? = null,
) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    private val followSync = ToggleSync { id, follow -> setFollow(id, follow) }
    private var generation = 0
    private var loadJob: Job? = null

    fun onIntent(intent: UserProfileIntent) {
        when (intent) {
            is UserProfileIntent.Load -> {
                val s = _state.value
                // 같은 사람을 다시 그릴 때(재구성·뒤로 왔을 때)는 다시 받지 않는다
                if (s.userId == intent.userId && (s.profile != null || s.isLoading && loadJob?.isActive == true)) return
                load(intent.userId)
            }
            is UserProfileIntent.Retry -> load(_state.value.userId)
            is UserProfileIntent.LoadMore -> loadMore()
            is UserProfileIntent.ToggleFollow -> toggleFollow()
            is UserProfileIntent.ToggleLike -> toggleLike(intent.postId)
            is UserProfileIntent.ToggleSave -> toggleSave(intent.postId)
            is UserProfileIntent.ArchivePost -> archive(intent.postId)
            is UserProfileIntent.CommentCountChanged -> reduce(UserProfileResult.CommentCountChanged(intent.postId, intent.delta))
            is UserProfileIntent.MessageShown -> reduce(UserProfileResult.Message(null))
            is UserProfileIntent.SetBlocked -> changeBlock(intent.block)
        }
    }

    /** 차단하면 서버가 팔로우도 끊고 글도 감춘다 → 성공하면 프로필을 처음부터 다시 받는다. */
    private fun changeBlock(block: Boolean) {
        val s = _state.value
        val profile = s.profile ?: return
        if (profile.isMe || s.isChangingBlock) return
        val gen = generation
        reduce(UserProfileResult.ChangingBlock)
        viewModelScope.launch {
            val result = runCatching { setBlock(profile.id, block) }
            if (gen != generation) return@launch
            result
                .onSuccess {
                    reduce(UserProfileResult.BlockChanged(profile.id, block))
                    reduce(UserProfileResult.Message(if (block) "차단했어요." else "차단을 풀었어요."))
                    load(profile.id)
                }
                .onFailure {
                    reduce(UserProfileResult.BlockChangeFailed)
                    reduce(UserProfileResult.Message(if (block) "차단하지 못했어요." else "차단을 풀지 못했어요."))
                }
        }
    }

    private fun load(userId: String) {
        val gen = ++generation
        loadJob?.cancel()
        reduce(UserProfileResult.Loading(userId))
        loadJob = viewModelScope.launch {
            // coroutineScope 로 감싸야 한쪽 실패가 여기서 잡힌다 — 맨 async 의 실패는 바깥 launch 까지 번져 앱이 죽는다
            val result = runCatching {
                coroutineScope {
                    val profile = async { getProfile(userId) }
                    val posts = async { getPosts(userId, 0) }
                    Triple(profile.await(), posts.await().first, posts.await().second)
                }
            }
            if (gen != generation) return@launch
            result
                .onSuccess { (profile, posts, next) -> reduce(UserProfileResult.Loaded(profile, posts, next)) }
                .onFailure { reduce(UserProfileResult.LoadFailed) }
        }
    }

    private fun loadMore() {
        val s = _state.value
        val offset = s.nextOffset ?: return
        if (s.isLoading || s.isLoadingMore) return
        val gen = generation
        reduce(UserProfileResult.LoadingMore)
        viewModelScope.launch {
            val result = runCatching { getPosts(s.userId, offset) }
            if (gen != generation) return@launch
            result
                .onSuccess { (posts, next) -> reduce(UserProfileResult.MoreLoaded(posts, next)) }
                .onFailure { reduce(UserProfileResult.LoadMoreFailed) }
        }
    }

    private fun toggleFollow() {
        val profile = _state.value.profile ?: return
        if (profile.isMe) return
        if (profile.blockedByMe) {
            reduce(UserProfileResult.Message("차단을 풀어야 팔로우할 수 있어요."))
            return
        }
        val gen = generation
        followSync.toggle(
            viewModelScope, profile.id, profile.followedByMe, profile.followerCount,
            onError = { if (gen == generation) reduce(UserProfileResult.Message(followErrorMessage(it))) },
        ) { following, count ->
            if (gen == generation) reduce(UserProfileResult.FollowChanged(following, count))
        }
    }

    private fun toggleLike(postId: String) {
        val post = _state.value.posts.firstOrNull { it.id == postId } ?: return
        likeSync.toggle(viewModelScope, InteractionTarget.POST, postId, post.liked, post.likes) { liked, likes ->
            reduce(UserProfileResult.LikeChanged(postId, liked, likes))
        }
    }

    private fun toggleSave(postId: String) {
        val post = _state.value.posts.firstOrNull { it.id == postId } ?: return
        saveSync?.toggle(viewModelScope, InteractionTarget.POST, postId, post.saved) { saved ->
            reduce(UserProfileResult.SaveChanged(postId, saved))
        }
    }

    /** 내 글 보관 — 바로 빼고, 실패하면 알린다 */
    private fun archive(postId: String) {
        val api = api ?: return
        reduce(UserProfileResult.PostRemoved(postId))
        viewModelScope.launch {
            if (runCatching { api.setArchived(postId, true) }.isFailure) {
                reduce(UserProfileResult.Message("보관하지 못했어요. 잠시 후 다시 시도해 주세요."))
                _state.value.profile?.id?.let { onIntent(UserProfileIntent.Load(it)) }
            }
        }
    }

    private fun reduce(result: UserProfileResult) = _state.update { UserProfileReducer.reduce(it, result) }
}

/** 팔로우 실패 안내 (프로필·팔로워 목록 공용) */
internal fun followErrorMessage(e: Throwable): String = when ((e as? FollowRejectedException)?.code) {
    "private_account" -> "비공개 계정은 아직 팔로우할 수 없어요."
    "blocked" -> "차단을 풀어야 팔로우할 수 있어요."
    "user_not_found" -> "사용자를 찾을 수 없어요."
    else -> "팔로우하지 못했어요. 로그인 상태를 확인해 주세요."
}
