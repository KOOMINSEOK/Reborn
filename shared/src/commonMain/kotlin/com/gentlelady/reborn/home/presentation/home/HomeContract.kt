package com.gentlelady.reborn.home.presentation.home
import com.gentlelady.reborn.home.domain.model.HomePost

data class HomeState(
    val isLoading: Boolean = false,
    val posts: List<HomePost> = emptyList(),
    val error: String? = null
)

sealed interface HomeIntent {
    object LoadFeed : HomeIntent
    object ClickMemorialIcon : HomeIntent
    data class ToggleLike(val postId: String) : HomeIntent
    /** 댓글 시트를 닫을 때 — 그동안 남기고 지운 만큼 댓글 수를 고친다 */
    data class CommentCountChanged(val postId: String, val delta: Int) : HomeIntent
    /** 차단 직후 — 서버에서 다시 받기 전에 그 사람 글을 바로 뺀다 */
    data class HideAuthor(val authorId: String) : HomeIntent
}

sealed interface HomeResult {
    object Loading : HomeResult
    data class FeedLoaded(val posts: List<HomePost>) : HomeResult
    data class Error(val message: String) : HomeResult
    data class LikeChanged(val postId: String, val liked: Boolean, val likes: Int) : HomeResult
    data class CommentCountChanged(val postId: String, val delta: Int) : HomeResult
    data class AuthorHidden(val authorId: String) : HomeResult
}