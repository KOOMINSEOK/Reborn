package com.gentlelady.reborn.management.archive.presentation

import androidx.lifecycle.ViewModel
import com.gentlelady.reborn.data.ArchiveMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ArchiveViewModel : ViewModel() {

    private val _state = MutableStateFlow(ArchiveState())
    val state: StateFlow<ArchiveState> = _state.asStateFlow()

    init {
        handleIntent(ArchiveIntent.LoadArchive)
    }

    fun handleIntent(intent: ArchiveIntent) {
        when (intent) {
            is ArchiveIntent.LoadArchive -> loadMockData()
            is ArchiveIntent.ClickBack -> { /* 상위 네비게이션에서 처리 */ }
            is ArchiveIntent.ClickPost -> { /* TODO: 보관된 게시물 상세/복원 화면 연동 */ }
        }
    }

    private fun loadMockData() {
        _state.update {
            it.copy(posts = ArchiveMockData.archivedPosts, isLoading = false)
        }
    }
}
