package com.gentlelady.reborn.interaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.interaction.domain.AddCommentUseCase
import com.gentlelady.reborn.interaction.domain.DeleteCommentUseCase
import com.gentlelady.reborn.interaction.domain.GetCommentsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 댓글 시트 하나 (글·추억 글 공용). [CommentsIntent.Open] 으로 대상이 바뀌면 처음부터.
 * 다른 글을 열면 이전 글에 대한 늦은 응답은 버린다 (generation).
 */
class CommentsViewModel(
    private val getComments: GetCommentsUseCase,
    private val addComment: AddCommentUseCase,
    private val deleteComment: DeleteCommentUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CommentsState())
    val state: StateFlow<CommentsState> = _state.asStateFlow()

    private var generation = 0
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
        val gen = generation
        val s = _state.value
        pageJob = viewModelScope.launch {
            runCatching { getComments(s.target, s.parentId, offset) }
                .onSuccess { (items, next) -> if (gen == generation) reduce(CommentsResult.PageLoaded(items, next, append)) }
                .onFailure { if (gen == generation) reduce(CommentsResult.LoadFailed) }
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
                .onSuccess { if (gen == generation) reduce(CommentsResult.Sent(it)) }
                .onFailure { if (gen == generation) reduce(CommentsResult.Failed("댓글을 남기지 못했어요. 로그인 상태를 확인해 주세요.")) }
        }
    }

    private fun delete(commentId: String) {
        val s = _state.value
        val gen = generation
        viewModelScope.launch {
            runCatching { deleteComment(s.target, commentId) }
                .onSuccess { if (gen == generation) reduce(CommentsResult.Deleted(commentId)) }
                .onFailure { if (gen == generation) reduce(CommentsResult.Failed("댓글을 지우지 못했어요.")) }
        }
    }

    private fun reduce(result: CommentsResult) = _state.update { CommentsReducer.reduce(it, result) }
}
