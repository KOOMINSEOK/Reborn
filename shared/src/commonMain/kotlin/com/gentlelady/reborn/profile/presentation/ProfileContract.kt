package com.gentlelady.reborn.profile.presentation

import org.jetbrains.compose.resources.DrawableResource

// 1. 단일 진실 공급원 (SSOT) 상태 정의
data class ProfileState(
    val username: String = "",
    val profileImageUrl: DrawableResource? = null,
    val backgroundImageUrl: DrawableResource? = null,
    val posthumousFeedCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val feeds: List<ProfileFeedItem> = emptyList(),
    val scheduledFeedCount: Int = 0,
    val isLoading: Boolean = false
)

data class ProfileFeedItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val thumbnail: DrawableResource,
    val likes: Int,
    val comments: Int
)

// 2. 유저 액션 인터페이스 (Intent)
sealed interface ProfileIntent {
    object LoadProfile : ProfileIntent
    object ClickEditBackground : ProfileIntent
    object ClickEditProfile : ProfileIntent
    object ClickViewAllFeeds : ProfileIntent
    data class ClickManagementMenu(val menuId: String) : ProfileIntent
}