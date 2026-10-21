package com.gentlelady.reborn.userprofile.presentation

import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.userprofile.domain.UserProfile

data class UserProfileState(
    val userId: String = "",
    val profile: UserProfile? = null,
    val posts: List<HomePost> = emptyList(),
    /** 다음 페이지 offset. null 이면 끝 */
    val nextOffset: Int? = null,
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val loadFailed: Boolean = false,
    /** 더 불러오기 실패 → 목록 끝에 "다시 시도" */
    val loadMoreFailed: Boolean = false,
    /** 한 번 보여주고 지우는 안내 (팔로우 실패 등) */
    val message: String? = null,
)

sealed interface UserProfileIntent {
    data class Load(val userId: String) : UserProfileIntent
    object Retry : UserProfileIntent
    object LoadMore : UserProfileIntent
    object ToggleFollow : UserProfileIntent
    data class ToggleLike(val postId: String) : UserProfileIntent
    data class CommentCountChanged(val postId: String, val delta: Int) : UserProfileIntent
    object MessageShown : UserProfileIntent
}

sealed interface UserProfileResult {
    data class Loading(val userId: String) : UserProfileResult
    data class Loaded(val profile: UserProfile, val posts: List<HomePost>, val nextOffset: Int?) : UserProfileResult
    object LoadFailed : UserProfileResult
    object LoadingMore : UserProfileResult
    data class MoreLoaded(val posts: List<HomePost>, val nextOffset: Int?) : UserProfileResult
    object LoadMoreFailed : UserProfileResult
    data class FollowChanged(val following: Boolean, val followerCount: Int) : UserProfileResult
    data class LikeChanged(val postId: String, val liked: Boolean, val likes: Int) : UserProfileResult
    data class CommentCountChanged(val postId: String, val delta: Int) : UserProfileResult
    data class Message(val text: String?) : UserProfileResult
}

object UserProfileReducer {
    fun reduce(state: UserProfileState, result: UserProfileResult): UserProfileState = when (result) {
        is UserProfileResult.Loading -> UserProfileState(userId = result.userId, isLoading = true)
        is UserProfileResult.Loaded -> state.copy(
            profile = result.profile,
            posts = result.posts,
            nextOffset = result.nextOffset,
            isLoading = false,
            loadFailed = false,
        )
        is UserProfileResult.LoadFailed -> state.copy(isLoading = false, loadFailed = true)
        is UserProfileResult.LoadingMore -> state.copy(isLoadingMore = true, loadMoreFailed = false)
        is UserProfileResult.MoreLoaded -> state.copy(
            posts = state.posts + result.posts.filter { new -> state.posts.none { it.id == new.id } },
            nextOffset = result.nextOffset,
            isLoadingMore = false,
        )
        is UserProfileResult.LoadMoreFailed -> state.copy(isLoadingMore = false, loadMoreFailed = true)
        is UserProfileResult.FollowChanged -> state.copy(
            profile = state.profile?.copy(followedByMe = result.following, followerCount = result.followerCount),
        )
        is UserProfileResult.LikeChanged -> state.updatePost(result.postId) { it.copy(liked = result.liked, likes = result.likes) }
        is UserProfileResult.CommentCountChanged ->
            state.updatePost(result.postId) { it.copy(comments = (it.comments + result.delta).coerceAtLeast(0)) }
        is UserProfileResult.Message -> state.copy(message = result.text)
    }

    private fun UserProfileState.updatePost(id: String, change: (HomePost) -> HomePost) =
        copy(posts = posts.map { if (it.id == id) change(it) else it })
}
