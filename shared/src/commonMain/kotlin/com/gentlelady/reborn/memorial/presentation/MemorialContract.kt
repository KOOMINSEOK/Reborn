// shared/src/commonMain/kotlin/com/gentlelady/reborn/memorial/presentation/MemorialContract.kt
package com.gentlelady.reborn.memorial.presentation

import org.jetbrains.compose.resources.DrawableResource

enum class MemorialOwnerType {
    OTHER_MEMORIAL,
    MY_MEMORIAL
}

enum class MemorialTab {
    HISTORY,
    GUESTBOOK
}

data class MemorialGuestBookItem(
    val id: String,
    val authorName: String,
    val authorProfileUrl: String?,
    val message: String,
    val timestamp: String
)

data class MemorialProfileData(
    val id: String = "",
    val name: String = "",
    val handle: String = "",
    val bio: String = "",
    val followerCount: Int = 0,
    val profileImageUrl: String? = null
)

data class MemorialState(
    val ownerType: MemorialOwnerType = MemorialOwnerType.OTHER_MEMORIAL,
    val profile: MemorialProfileData = MemorialProfileData(),
    val selectedTab: MemorialTab = MemorialTab.HISTORY,
    val historyImages: List<DrawableResource> = emptyList(), // DrawableResource 타입으로 변경
    val guestBookMessages: List<MemorialGuestBookItem> = emptyList(),
    val guestBookInputText: String = "",
    val isLoading: Boolean = false
)

sealed interface MemorialIntent {
    object ClickBack : MemorialIntent
    object ClickMusic : MemorialIntent
    object ClickMore : MemorialIntent
    data class SelectTab(val tab: MemorialTab) : MemorialIntent
    data class UpdateGuestBookInput(val text: String) : MemorialIntent
    object SubmitGuestBook : MemorialIntent
    object ClickEditProfile : MemorialIntent
    object ClickTribute : MemorialIntent
    data class ClickHistoryImage(val index: Int) : MemorialIntent
}