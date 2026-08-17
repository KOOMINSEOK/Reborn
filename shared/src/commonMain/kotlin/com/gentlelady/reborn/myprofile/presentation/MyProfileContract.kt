package com.gentlelady.reborn.myprofile.presentation

import org.jetbrains.compose.resources.DrawableResource
import com.gentlelady.reborn.myprofile.domain.model.MyProfileFeedItem // 💡 새로 분리한 도메인 모델 임포트

data class MyProfileState(
    val username: String = "",
    val displayName: String = "",
    val profileImageUrl: DrawableResource? = null,
    val backgroundImageUrl: DrawableResource? = null,
    val posthumousFeedCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val scheduledFeedCount: Int = 0,
    val feeds: List<MyProfileFeedItem> = emptyList(), // 💡 독립 모델 매칭 완료
    val isLoading: Boolean = true
)

sealed interface MyProfileIntent {
    object LoadProfile : MyProfileIntent
    object ClickEditBackground : MyProfileIntent
    object ClickEditProfile : MyProfileIntent
    object ClickToggleMemorialMode : MyProfileIntent
    object ClickViewAllFeeds : MyProfileIntent
    data class ClickManagementMenu(val menuId: String) : MyProfileIntent
}
