package com.gentlelady.reborn.management.archive.presentation

import com.gentlelady.reborn.management.archive.domain.model.ArchivedPostItem

data class ArchiveState(
    val posts: List<ArchivedPostItem> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface ArchiveIntent {
    object LoadArchive : ArchiveIntent
    object ClickBack : ArchiveIntent
    data class ClickPost(val postId: String) : ArchiveIntent
}
