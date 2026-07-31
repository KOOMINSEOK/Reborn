// shared/src/commonMain/kotlin/com/gentlelady/reborn/memorial/presentation/MemorialContract.kt
package com.gentlelady.reborn.memorial.presentation

import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.DrawableResource

enum class MemorialTab {
    HISTORY,
    ONLINE_WREATH,
    GUESTBOOK
}

data class MemorialGuestBookItem(
    val id: String,
    val authorName: String,
    val authorProfileUrl: String?,
    val message: String,
    val timestamp: String
)

data class MemorialHistoryItem(
    val id: String,
    val authorName: String,
    val date: String,
    val caption: String,
    val imageRes: DrawableResource? = null, // 앱 번들 목업 이미지 (프리뷰/목데이터용)
    val imageBitmap: ImageBitmap? = null, // 갤러리에서 직접 등록한 이미지 (imageRes보다 우선 적용)
    val authorProfileRes: DrawableResource? = null,
    val likes: Int = 0,
    val comments: Int = 0
)

data class MemorialWreathItem(
    val id: String,
    val organizationName: String // 화환을 보낸 단체/보내는 사람 이름 (앨범 타일에 표시될 텍스트)
)

data class MemorialProfileData(
    val id: String = "",
    val name: String = "",
    val handle: String = "",
    val bio: String = "",
    val followerCount: Int = 0,
    val profileImageRes: DrawableResource? = null
)

data class EditProfileFormState(
    val name: String = "",
    val handle: String = "",
    val bio: String = "",
    val profileImageRes: DrawableResource? = null
)

data class MemorialHistoryWriteFormState(
    val imageBitmap: ImageBitmap? = null,
    val caption: String = ""
)

data class MemorialState(
    val profile: MemorialProfileData = MemorialProfileData(),
    val selectedTab: MemorialTab = MemorialTab.HISTORY,
    val historyItems: List<MemorialHistoryItem> = emptyList(),
    val historyCount: Int = 0,
    val selectedHistoryIndex: Int? = null, // null이 아니면 히스토리 상세(추억 보기) 화면 표시
    val onlineWreathItems: List<MemorialWreathItem> = emptyList(),
    val onlineWreathCount: Int = 0,
    val guestBookMessages: List<MemorialGuestBookItem> = emptyList(),
    val guestBookCount: Int = 0,
    val guestBookInputText: String = "",
    val isEditingProfile: Boolean = false, // 프로필 편집 모드 화면 전환 플래그
    val editFormState: EditProfileFormState = EditProfileFormState(),
    val isWritingHistory: Boolean = false, // 히스토리 작성 화면 전환 플래그
    val historyWriteFormState: MemorialHistoryWriteFormState = MemorialHistoryWriteFormState(),
    val isLoading: Boolean = false
)

sealed interface MemorialIntent {
    object ClickBack : MemorialIntent
    object ClickShare : MemorialIntent
    object ClickMusic : MemorialIntent
    object ClickMore : MemorialIntent
    data class SelectTab(val tab: MemorialTab) : MemorialIntent
    data class UpdateGuestBookInput(val text: String) : MemorialIntent
    object SubmitGuestBook : MemorialIntent
    object ClickEditProfile : MemorialIntent
    object ClickTribute : MemorialIntent
    object ClickPurchaseWreath : MemorialIntent
    data class ClickHistoryImage(val index: Int) : MemorialIntent

    // 프로필 편집 화면 전용 인텐트
    data class UpdateEditName(val name: String) : MemorialIntent
    data class UpdateEditHandle(val handle: String) : MemorialIntent
    data class UpdateEditBio(val bio: String) : MemorialIntent
    object ClickChangeProfileImage : MemorialIntent
    object ClickSaveProfile : MemorialIntent
    object ClickCloseEditProfile : MemorialIntent

    // 히스토리 작성 화면 전용 인텐트
    object ClickAddHistory : MemorialIntent
    data class UpdateHistoryWriteImage(val imageBitmap: ImageBitmap) : MemorialIntent
    object ClickRemoveHistoryImage : MemorialIntent
    data class UpdateHistoryWriteCaption(val caption: String) : MemorialIntent
    object ClickPostHistory : MemorialIntent
    object ClickCloseHistoryWrite : MemorialIntent
}