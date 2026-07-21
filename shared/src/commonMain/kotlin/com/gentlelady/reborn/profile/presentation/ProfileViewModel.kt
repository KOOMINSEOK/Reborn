package com.gentlelady.reborn.profile.presentation

import androidx.lifecycle.ViewModel
import com.gentlelady.reborn.data.MockDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        handleIntent(ProfileIntent.LoadProfile)
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadProfile -> loadMockProfileData()
            is ProfileIntent.ClickEditBackground -> { /* 배경 편집 비즈니스 로직 */ }
            is ProfileIntent.ClickEditProfile -> { /* 프로필 편집 비즈니스 로직 */ }
            is ProfileIntent.ClickToggleMemorialMode -> { /* 메모리얼 모드 전환 로직 */ }
            is ProfileIntent.ClickViewAllFeeds -> { /* 피드 전체보기 액션 */ }
            is ProfileIntent.ClickManagementMenu -> { /* 메뉴 분기 비즈니스 */ }
        }
    }

    private fun loadMockProfileData() {
        _state.update {
            it.copy(
                username = MockDataSource.profileUsername,
                profileImageUrl = MockDataSource.profileImage,
                backgroundImageUrl = MockDataSource.profileBgImage,
                posthumousFeedCount = MockDataSource.posthumousFeedCount,
                followersCount = MockDataSource.followersCount,
                followingCount = MockDataSource.followingCount,
                feeds = MockDataSource.profileFeeds,
                scheduledFeedCount = MockDataSource.scheduledFeedCount,
                isLoading = false
            )
        }
    }
}
