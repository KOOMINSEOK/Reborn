package com.gentlelady.reborn.profile.presentation

import org.jetbrains.compose.resources.DrawableResource
import com.gentlelady.reborn.profile.domain.model.ProfileFeedItem // 💡 새로 분리한 도메인 모델 임포트

data class ProfileState(
    val username: String = "",
    val profileImageUrl: DrawableResource? = null,
    val backgroundImageUrl: DrawableResource? = null,
    val posthumousFeedCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val scheduledFeedCount: Int = 0,
    val feeds: List<ProfileFeedItem> = emptyList(), // 💡 독립 모델 매칭 완료
    val isLoading: Boolean = true
)

sealed interface ProfileIntent {
    object LoadProfile : ProfileIntent
    object ClickEditBackground : ProfileIntent
    object ClickEditProfile : ProfileIntent
    object ClickToggleMemorialMode : ProfileIntent
    object ClickViewAllFeeds : ProfileIntent
    data class ClickManagementMenu(val menuId: String) : ProfileIntent
}