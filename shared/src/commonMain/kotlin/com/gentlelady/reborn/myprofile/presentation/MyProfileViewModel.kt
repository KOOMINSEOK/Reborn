package com.gentlelady.reborn.myprofile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.network.dto.toAbsoluteServerUrl
import com.gentlelady.reborn.data.MockDataSource
import com.gentlelady.reborn.myprofile.data.MyProfileRepository
import com.gentlelady.reborn.myprofile.domain.usecase.UpdateAvatarUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MyProfileViewModel(
    private val repository: MyProfileRepository,
    private val updateAvatarUseCase: UpdateAvatarUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MyProfileState())
    val state: StateFlow<MyProfileState> = _state.asStateFlow()

    init {
        handleIntent(MyProfileIntent.LoadProfile)
    }

    fun handleIntent(intent: MyProfileIntent) {
        when (intent) {
            is MyProfileIntent.LoadProfile -> {
                loadMockProfileData()
                loadServerProfile()
            }
            is MyProfileIntent.ChangeAvatar -> changeAvatar(intent.jpeg)
            is MyProfileIntent.ClickEditBackground -> { /* 배경 편집 비즈니스 로직 */ }
            is MyProfileIntent.ClickEditProfile -> { /* 프로필 편집 비즈니스 로직 */ }
            is MyProfileIntent.ClickToggleMemorialMode -> { /* 메모리얼 모드 전환 로직 */ }
            is MyProfileIntent.ClickViewAllFeeds -> { /* 피드 전체보기 액션 */ }
            is MyProfileIntent.ClickManagementMenu -> { /* 메뉴 분기 비즈니스 */ }
        }
    }

    /** 서버 프로필로 이름·핸들·사진·팔로워 수를 덮는다. 피드 등 나머지는 아직 목업. 실패하면 목업 그대로. */
    private fun loadServerProfile() {
        viewModelScope.launch {
            val profile = repository.getMyProfile() ?: return@launch
            _state.update {
                it.copy(
                    username = profile.handle,
                    displayName = profile.displayName,
                    avatarUrl = profile.avatarUrl.toAbsoluteServerUrl(),
                    followersCount = profile.followerCount,
                    followingCount = profile.followingCount,
                )
            }
        }
    }

    private fun changeAvatar(jpeg: ByteArray) {
        if (_state.value.isUpdatingAvatar) return
        _state.update { it.copy(isUpdatingAvatar = true, avatarError = null) }
        viewModelScope.launch {
            val result = updateAvatarUseCase(jpeg)
            _state.update {
                when (result) {
                    is UpdateAvatarUseCase.Result.Updated ->
                        it.copy(avatarUrl = result.avatarUrl, isUpdatingAvatar = false)
                    is UpdateAvatarUseCase.Result.Failed ->
                        it.copy(isUpdatingAvatar = false, avatarError = result.message)
                }
            }
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
