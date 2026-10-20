package com.gentlelady.reborn.interaction.presentation

import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.interaction.domain.Comment

data class CommentsState(
    val target: InteractionTarget = InteractionTarget.POST,
    val parentId: String = "",
    val comments: List<Comment> = emptyList(),
    /** 다음 페이지 offset. null 이면 끝까지 받음 */
    val nextOffset: Int? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val loadFailed: Boolean = false,
    val input: String = "",
    val isSending: Boolean = false,
    val error: String? = null,
    /** 이번에 열고 나서 늘어난(+)/줄어든(-) 댓글 수 — 닫을 때 글의 댓글 수에 더한다 */
    val countDelta: Int = 0,
)

sealed interface CommentsIntent {
    data class Open(val target: InteractionTarget, val parentId: String) : CommentsIntent
    object LoadMore : CommentsIntent
    data class UpdateInput(val text: String) : CommentsIntent
    object Send : CommentsIntent
    data class Delete(val commentId: String) : CommentsIntent
}

sealed interface CommentsResult {
    data class Opened(val target: InteractionTarget, val parentId: String) : CommentsResult
    data class PageLoaded(val comments: List<Comment>, val nextOffset: Int?, val append: Boolean) : CommentsResult
    object LoadFailed : CommentsResult
    object LoadingMore : CommentsResult
    data class InputChanged(val text: String) : CommentsResult
    object Sending : CommentsResult
    data class Sent(val comment: Comment) : CommentsResult
    data class Deleted(val commentId: String) : CommentsResult
    data class Failed(val message: String) : CommentsResult
}

object CommentsReducer {
    fun reduce(state: CommentsState, result: CommentsResult): CommentsState = when (result) {
        is CommentsResult.Opened -> CommentsState(target = result.target, parentId = result.parentId, isLoading = true)
        is CommentsResult.PageLoaded -> state.copy(
            comments = if (result.append) state.comments + result.comments.filter { new -> state.comments.none { it.id == new.id } }
            else result.comments,
            nextOffset = result.nextOffset,
            isLoading = false,
            isLoadingMore = false,
            loadFailed = false,
        )
        is CommentsResult.LoadFailed -> state.copy(isLoading = false, isLoadingMore = false, loadFailed = state.comments.isEmpty())
        is CommentsResult.LoadingMore -> state.copy(isLoadingMore = true)
        is CommentsResult.InputChanged -> state.copy(input = result.text, error = null)
        is CommentsResult.Sending -> state.copy(isSending = true, error = null)
        is CommentsResult.Sent -> state.copy(
            // 끝까지 받은 목록이면 맨 아래(최신)에 붙인다. 아니면 스크롤로 받을 때 온다.
            // (새 댓글은 목록 맨 끝이라 앞쪽 offset 은 그대로)
            comments = if (state.nextOffset == null) state.comments + result.comment else state.comments,
            input = "",
            isSending = false,
            countDelta = state.countDelta + 1,
        )
        is CommentsResult.Deleted -> state.copy(
            comments = state.comments.filterNot { it.id == result.commentId },
            nextOffset = state.nextOffset?.minus(1),
            countDelta = state.countDelta - 1,
        )
        is CommentsResult.Failed -> state.copy(isSending = false, error = result.message)
    }
}
