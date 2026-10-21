package com.gentlelady.reborn.interaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.interaction.domain.AddCommentUseCase
import com.gentlelady.reborn.interaction.domain.DeleteCommentUseCase
import com.gentlelady.reborn.interaction.domain.GetCommentsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 댓글 시트 하나 (글·추억 글 공용). [CommentsIntent.Open] 으로 대상이 바뀌면 처음부터.
 * - 다른 글을 열면 이전 글에 대한 늦은 응답은 목록에 반영하지 않는다 (generation).
 * - 삭제가 성공하면 받던 다음 페이지 응답은 버린다 (offset 이 밀려 하나를 건너뛰게 되므로, pageGeneration).
 * - 서버에 반영된 댓글 수 변화는 [countChanges] 로 — 시트를 닫았거나 다른 글로 바꾼 뒤에 끝나도 전달된다.
 */
class CommentsViewModel(
    private val getComments: GetCommentsUseCase,
    private val addComment: AddCommentUseCase,
    private val deleteComment: DeleteCommentUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CommentsState())
    val state: StateFlow<CommentsState> = _state.asStateFlow()

    private val _countChanges = MutableSharedFlow<CommentCountChange>(extraBufferCapacity = 16)
    val countChanges: SharedFlow<CommentCountChange> = _countChanges.asSharedFlow()

    private var generation = 0
    private var pageGeneration = 0
    private var pageJob: Job? = null

    fun onIntent(intent: CommentsIntent) {
        when (intent) {
            is CommentsIntent.Open -> open(intent)
            is CommentsIntent.LoadMore -> loadMore()
            is CommentsIntent.UpdateInput -> if (!_state.value.isSending) reduce(CommentsResult.InputChanged(intent.text))
            is CommentsIntent.Send -> send()
            is CommentsIntent.Delete -> delete(intent.commentId)
        }
    }

    private fun open(intent: CommentsIntent.Open) {
        generation++
        pageGeneration++
        pageJob?.cancel()
        reduce(CommentsResult.Opened(intent.target, intent.parentId))
        loadPage(offset = 0, append = false)
    }

    private fun loadMore() {
        val s = _state.value
        val offset = s.nextOffset ?: return
        if (s.isLoading || s.isLoadingMore) return
        reduce(CommentsResult.LoadingMore)
        loadPage(offset, append = true)
    }

    private fun loadPage(offset: Int, append: Boolean) {
        val gen = pageGeneration
        val s = _state.value
        pageJob = viewModelScope.launch {
            val result = runCatching { getComments(s.target, s.parentId, offset) }
            if (gen != pageGeneration) return@launch
            result
                .onSuccess { (items, next) -> reduce(CommentsResult.PageLoaded(items, next, append)) }
                .onFailure { reduce(CommentsResult.LoadFailed) }
        }
    }

    private fun send() {
        val s = _state.value
        val body = s.input.trim()
        if (body.isEmpty() || s.isSending) return
        val gen = generation
        reduce(CommentsResult.Sending)
        viewModelScope.launch {
            runCatching { addComment(s.target, s.parentId, body) }
                .onSuccess {
                    _countChanges.tryEmit(CommentCountChange(s.target, s.parentId, +1))
                    if (gen == generation) reduce(CommentsResult.Sent(it))
                }
                .onFailure {
                    if (gen == generation) reduce(CommentsResult.SendFailed("댓글을 남기지 못했어요. 로그인 상태를 확인해 주세요."))
                }
        }
    }

    private fun delete(commentId: String) {
        val s = _state.value
        if (commentId in s.deletingIds) return
        val gen = generation
        reduce(CommentsResult.Deleting(commentId))
        viewModelScope.launch {
            runCatching { deleteComment(s.target, commentId) }
                .onSuccess {
                    _countChanges.tryEmit(CommentCountChange(s.target, s.parentId, -1))
                    if (gen != generation) return@onSuccess
                    if (_state.value.isLoadingMore) {
                        pageGeneration++
                        pageJob?.cancel()
                        reduce(CommentsResult.LoadMoreDropped)
                    }
                    reduce(CommentsResult.Deleted(commentId))
                }
                .onFailure { if (gen == generation) reduce(CommentsResult.DeleteFailed(commentId, "댓글을 지우지 못했어요.")) }
        }
    }

    private fun reduce(result: CommentsResult) = _state.update { CommentsReducer.reduce(it, result) }
}
