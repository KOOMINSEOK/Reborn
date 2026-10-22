package com.gentlelady.reborn.home.presentation.home
import com.gentlelady.reborn.home.domain.model.HomePost

data class HomeState(
    val isLoading: Boolean = false,
    val posts: List<HomePost> = emptyList(),
    val error: String? = null,
    /** 안 읽은 알림 수 (종 버튼의 파란 점). 로그인 안 함·서버 꺼짐이면 0 */
    val unreadNotifications: Int = 0,
)

sealed interface HomeIntent {
    object LoadFeed : HomeIntent
    object ClickMemorialIcon : HomeIntent
    data class ToggleLike(val postId: String) : HomeIntent
    data class ToggleSave(val postId: String) : HomeIntent
    /** 내 글을 보관함으로 (피드에서 숨김) */
    data class ArchivePost(val postId: String) : HomeIntent
    /** 댓글 시트를 닫을 때 — 그동안 남기고 지운 만큼 댓글 수를 고친다 */
    data class CommentCountChanged(val postId: String, val delta: Int) : HomeIntent
    /** 차단 직후 — 서버에서 다시 받기 전에 그 사람 글을 바로 뺀다 */
    data class HideAuthor(val authorId: String) : HomeIntent
    /** 홈으로 돌아올 때마다 (알림을 읽고 왔을 수 있다) */
    object RefreshUnreadNotifications : HomeIntent
}

sealed interface HomeResult {
    object Loading : HomeResult
    data class FeedLoaded(val posts: List<HomePost>) : HomeResult
    data class Error(val message: String) : HomeResult
    data class LikeChanged(val postId: String, val liked: Boolean, val likes: Int) : HomeResult
    data class SaveChanged(val postId: String, val saved: Boolean) : HomeResult
    data class PostRemoved(val postId: String) : HomeResult
    data class CommentCountChanged(val postId: String, val delta: Int) : HomeResult
    data class AuthorHidden(val authorId: String) : HomeResult
    data class UnreadNotifications(val count: Int) : HomeResult
}