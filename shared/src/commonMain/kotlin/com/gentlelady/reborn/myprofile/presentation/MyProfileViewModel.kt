package com.gentlelady.reborn.myprofile.presentation

import androidx.lifecycle.ViewModel
import com.gentlelady.reborn.data.MockDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MyProfileViewModel : ViewModel() {

    private val _state = MutableStateFlow(MyProfileState())
    val state: StateFlow<MyProfileState> = _state.asStateFlow()

    init {
        handleIntent(MyProfileIntent.LoadProfile)
    }

    fun handleIntent(intent: MyProfileIntent) {
        when (intent) {
            is MyProfileIntent.LoadProfile -> loadMockProfileData()
            is MyProfileIntent.ClickEditBackground -> { /* 배경 편집 비즈니스 로직 */ }
            is MyProfileIntent.ClickEditProfile -> { /* 프로필 편집 비즈니스 로직 */ }
            is MyProfileIntent.ClickToggleMemorialMode -> { /* 메모리얼 모드 전환 로직 */ }
            is MyProfileIntent.ClickViewAllFeeds -> { /* 피드 전체보기 액션 */ }
            is MyProfileIntent.ClickManagementMenu -> { /* 메뉴 분기 비즈니스 */ }
        }
    }

    private fun loadMockProfileData() {
        _state.update {
            it.copy(
                username = MockDataSource.myProfileUsername,
                displayName = MockDataSource.myProfileDisplayName,
                profileImageUrl = MockDataSource.myProfileImage,
                backgroundImageUrl = MockDataSource.myProfileBgImage,
                posthumousFeedCount = MockDataSource.posthumousFeedCount,
                followersCount = MockDataSource.followersCount,
                followingCount = MockDataSource.followingCount,
                feeds = MockDataSource.myProfileFeeds,
                scheduledFeedCount = MockDataSource.scheduledFeedCount,
                isLoading = false
            )
        }
    }
}
