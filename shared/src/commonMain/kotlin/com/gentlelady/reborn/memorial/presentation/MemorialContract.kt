// shared/src/commonMain/kotlin/com/gentlelady/reborn/memorial/presentation/MemorialContract.kt
package com.gentlelady.reborn.memorial.presentation

import org.jetbrains.compose.resources.DrawableResource

/** 서버 미연결 시 폴백에 쓰는, 데모용 시드 메모리얼 id. */
const val DEMO_MEMORIAL_ID = "00000000-0000-0000-0000-0000000000c1"

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
    val timestamp: String,
    /** 내가 쓴 방명록이면 오른쪽 말풍선 */
    val isMine: Boolean = false,
)

data class MemorialHistoryItem(
    val id: String,
    val authorName: String,
    val date: String,
    val caption: String,
    val imageUrls: List<String> = emptyList(), // 서버 이미지 URL 들 (순서 = 표시 순서)
    val imageRes: DrawableResource? = null, // 앱 번들 목업 이미지 (프리뷰/목데이터용)
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
    val profileImageUrl: String? = null, // 서버 이미지 URL
    val profileImageRes: DrawableResource? = null
)

data class MemorialHistoryWriteFormState(
    /** 고른 사진(업로드용 JPEG). 미리보기는 Coil 이 바이트를 그대로 그린다. */
    val image: ByteArray? = null,
    val caption: String = "",
    val isPosting: Boolean = false,
    val error: String? = null,
)

data class MemorialState(
    val memorialId: String = DEMO_MEMORIAL_ID,
    val profile: MemorialProfileData = MemorialProfileData(),
    val selectedTab: MemorialTab = MemorialTab.HISTORY,
    val historyItems: List<MemorialHistoryItem> = emptyList(),
    val selectedHistoryIndex: Int? = null, // null이 아니면 히스토리 상세(추억 보기) 화면 표시
    val onlineWreathItems: List<MemorialWreathItem> = emptyList(),
    val guestBookMessages: List<MemorialGuestBookItem> = emptyList(),
    val guestBookInputText: String = "",
    val isSubmittingGuestBook: Boolean = false,
    val guestBookError: String? = null,
    val isWritingHistory: Boolean = false, // 히스토리 작성 화면 전환 플래그
    val historyWriteFormState: MemorialHistoryWriteFormState = MemorialHistoryWriteFormState(),
    val isLoading: Boolean = false
)

sealed interface MemorialIntent {
    data class LoadMemorial(val memorialId: String) : MemorialIntent
    object ClickBack : MemorialIntent
    object ClickShare : MemorialIntent
    object ClickMusic : MemorialIntent
    object ClickMore : MemorialIntent
    data class SelectTab(val tab: MemorialTab) : MemorialIntent
    data class UpdateGuestBookInput(val text: String) : MemorialIntent
    object SubmitGuestBook : MemorialIntent
    object ClickTribute : MemorialIntent
    object ClickPurchaseWreath : MemorialIntent
    data class AddOnlineWreathItem(val item: MemorialWreathItem) : MemorialIntent
    data class ClickHistoryImage(val index: Int) : MemorialIntent

    // 히스토리 작성 화면 전용 인텐트
    object ClickAddHistory : MemorialIntent
    data class UpdateHistoryWriteImage(val jpeg: ByteArray) : MemorialIntent
    object ClickRemoveHistoryImage : MemorialIntent
    data class UpdateHistoryWriteCaption(val caption: String) : MemorialIntent
    object ClickPostHistory : MemorialIntent
    object ClickCloseHistoryWrite : MemorialIntent
}
