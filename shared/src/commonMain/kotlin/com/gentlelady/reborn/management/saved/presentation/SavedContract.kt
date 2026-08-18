package com.gentlelady.reborn.management.saved.presentation

import com.gentlelady.reborn.management.saved.domain.model.SavedGridItem
import com.gentlelady.reborn.management.saved.domain.model.SavedGuestBookItem

enum class SavedTab { POSTS, HISTORY, GUESTBOOK }

data class SavedState(
    val selectedTab: SavedTab = SavedTab.POSTS,
    val posts: List<SavedGridItem> = emptyList(),
    val history: List<SavedGridItem> = emptyList(),
    val guestBookEntries: List<SavedGuestBookItem> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface SavedIntent {
    object LoadSaved : SavedIntent
    object ClickBack : SavedIntent
    data class ClickTab(val tab: SavedTab) : SavedIntent
    data class ClickPost(val postId: String) : SavedIntent
    data class ClickHistoryItem(val historyId: String) : SavedIntent
    data class ClickGuestBookEntry(val entryId: String) : SavedIntent
}
