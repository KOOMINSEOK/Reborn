package com.gentlelady.reborn.myprofile.presentation

import org.jetbrains.compose.resources.DrawableResource
import com.gentlelady.reborn.myprofile.domain.model.MyProfileFeedItem // 💡 새로 분리한 도메인 모델 임포트

data class MyProfileState(
    val username: String = "",
    val displayName: String = "",
    /** 한 줄 소개 (서버) */
    val bio: String = "",
    val isSavingProfileText: Boolean = false,
    val profileTextError: String? = null,
    /** 닉네임 저장에 성공할 때마다 올린다 → 닉네임 편집 화면이 닫힌다 (소개와 따로 — 다른 화면을 닫지 않게) */
    val displayNameSavedToken: Int = 0,
    /** 한 줄 소개 저장에 성공할 때마다 올린다 → 소개 편집 화면이 닫힌다 */
    val bioSavedToken: Int = 0,
    val profileImageUrl: DrawableResource? = null, // 목업 아바타 (서버 사진이 없을 때)
    /** 서버 아바타 URL. 있으면 [profileImageUrl] 보다 우선. */
    val avatarUrl: String? = null,
    val isUpdatingAvatar: Boolean = false,
    val avatarError: String? = null,
    val backgroundImageUrl: DrawableResource? = null,
    val posthumousFeedCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val scheduledFeedCount: Int = 0,
    val feeds: List<MyProfileFeedItem> = emptyList(), // 💡 독립 모델 매칭 완료
    /** 서버의 내 id. 있으면 팔로워 목록·전체 글 화면으로 갈 수 있다 (목업이면 null) */
    val myUserId: String? = null,
    val isLoading: Boolean = true
)

sealed interface MyProfileIntent {
    object LoadProfile : MyProfileIntent
    object ClickEditBackground : MyProfileIntent
    object ClickEditProfile : MyProfileIntent
    object ClickToggleMemorialMode : MyProfileIntent
    object ClickViewAllFeeds : MyProfileIntent
    object ClickFollowers : MyProfileIntent
    object ClickFollowing : MyProfileIntent
    data class ClickManagementMenu(val menuId: String) : MyProfileIntent
    /** 갤러리에서 고른 사진(업로드용 JPEG)으로 프로필 사진 바꾸기 */
    data class ChangeAvatar(val jpeg: ByteArray) : MyProfileIntent
    data class SaveDisplayName(val displayName: String) : MyProfileIntent
    data class SaveBio(val bio: String) : MyProfileIntent
    /** 편집 화면을 열 때 — 지난 오류 문구를 지운다 */
    object StartEditProfileText : MyProfileIntent
}
