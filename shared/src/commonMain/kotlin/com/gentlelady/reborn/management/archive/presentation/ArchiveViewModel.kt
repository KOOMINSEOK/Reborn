package com.gentlelady.reborn.management.archive.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.toAbsoluteServerUrl
import com.gentlelady.reborn.data.ArchiveMockData
import com.gentlelady.reborn.management.archive.domain.model.ArchivedPostItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** 게시물 보관함. 앱 전체에서 하나 — 들어올 때마다 새로, 계정이 바뀌면 비운다. 둘러보기면 목업. */
class ArchiveViewModel(
    private val api: RebornApi,
    private val currentUserId: CurrentUserIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ArchiveState())
    val state: StateFlow<ArchiveState> = _state.asStateFlow()
    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            currentUserId.changes.collect {
                _state.value = ArchiveState()
                load()
            }
        }
    }

    fun handleIntent(intent: ArchiveIntent) {
        when (intent) {
            is ArchiveIntent.LoadArchive -> load()
            is ArchiveIntent.ClickBack -> Unit // 상위 네비게이션에서 처리
            is ArchiveIntent.ClickPost -> _state.update { s -> s.copy(restoreTarget = s.posts.firstOrNull { it.id == intent.postId }) }
            is ArchiveIntent.DismissRestore -> _state.update { it.copy(restoreTarget = null) }
            is ArchiveIntent.ConfirmRestore -> restore()
            is ArchiveIntent.MessageShown -> _state.update { it.copy(message = null) }
        }
    }

    private fun load() {
        loadJob?.cancel()
        if (currentUserId() == null) {
            _state.update { it.copy(posts = ArchiveMockData.archivedPosts, isLoading = false) }
            return
        }
        loadJob = viewModelScope.launch {
            val posts = runCatching { api.archivedPosts().items }.getOrNull()
            _state.update {
                if (posts == null) it.copy(isLoading = false, message = "보관함을 불러오지 못했어요.")
                else it.copy(
                    isLoading = false,
                    posts = posts.map { p ->
                        ArchivedPostItem(p.id, imageUrl = p.imageUrls.firstOrNull().toAbsoluteServerUrl(), caption = p.caption)
                    },
                )
            }
        }
    }

    /** 되돌리면 피드·프로필에 다시 보인다. 바로 빼고, 실패하면 다시 넣는다 */
    private fun restore() {
        val target = _state.value.restoreTarget ?: return
        if (currentUserId() == null) return
        _state.update { s -> s.copy(restoreTarget = null, posts = s.posts.filterNot { it.id == target.id }) }
        viewModelScope.launch {
            if (runCatching { api.setArchived(target.id, false) }.isFailure) {
                _state.update { it.copy(message = "되돌리지 못했어요. 잠시 후 다시 시도해 주세요.") }
                load()
            } else {
                _state.update { it.copy(message = "게시물을 다시 보이게 했어요.") }
            }
        }
    }
}
