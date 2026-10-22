package com.gentlelady.reborn.management.archive.presentation

import com.gentlelady.reborn.management.archive.domain.model.ArchivedPostItem

data class ArchiveState(
    val posts: List<ArchivedPostItem> = emptyList(),
    val isLoading: Boolean = true,
    /** 되돌릴지 묻는 중인 글 */
    val restoreTarget: ArchivedPostItem? = null,
    val message: String? = null,
)

sealed interface ArchiveIntent {
    object LoadArchive : ArchiveIntent
    object ClickBack : ArchiveIntent
    /** 글을 누르면 되돌릴지 묻는다 */
    data class ClickPost(val postId: String) : ArchiveIntent
    object ConfirmRestore : ArchiveIntent
    object DismissRestore : ArchiveIntent
    object MessageShown : ArchiveIntent
}
