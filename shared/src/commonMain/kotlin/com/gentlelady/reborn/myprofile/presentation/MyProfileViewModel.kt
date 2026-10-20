package com.gentlelady.reborn.myprofile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.network.dto.toAbsoluteServerUrl
import com.gentlelady.reborn.data.MockDataSource
import com.gentlelady.reborn.myprofile.data.MyProfileRepository
import com.gentlelady.reborn.myprofile.domain.usecase.UpdateAvatarUseCase
import com.gentlelady.reborn.myprofile.domain.usecase.UpdateProfileTextUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MyProfileViewModel(
    private val repository: MyProfileRepository,
    private val updateAvatarUseCase: UpdateAvatarUseCase,
    private val updateProfileTextUseCase: UpdateProfileTextUseCase,
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
            is MyProfileIntent.SaveDisplayName -> saveText(displayName = intent.displayName)
            is MyProfileIntent.SaveBio -> saveText(bio = intent.bio)
            is MyProfileIntent.StartEditProfileText -> _state.update { it.copy(profileTextError = null) }
            is MyProfileIntent.ClickEditBackground -> { /* 배경 편집 비즈니스 로직 */ }
            is MyProfileIntent.ClickEditProfile -> { /* 프로필 편집 비즈니스 로직 */ }
            is MyProfileIntent.ClickToggleMemorialMode -> { /* 메모리얼 모드 전환 로직 */ }
            is MyProfileIntent.ClickViewAllFeeds -> { /* 피드 전체보기 액션 */ }
            is MyProfileIntent.ClickManagementMenu -> { /* 메뉴 분기 비즈니스 */ }
        }
    }

    private var profileJob: Job? = null
    /** 아바타를 바꾸기 시작할 때마다 올린다 — 그 전에 출발한 조회 응답이 새 사진을 옛 사진으로 덮지 않게 */
    private var avatarVersion = 0

    /** 서버 프로필로 이름·핸들·사진·팔로워 수를 덮는다. 피드 등 나머지는 아직 목업. 실패하면 목업 그대로. */
    private fun loadServerProfile() {
        profileJob?.cancel() // 조회는 하나만 — 늦게 온 옛 응답이 덮지 않게
        val version = avatarVersion
        profileJob = viewModelScope.launch {
            val profile = repository.getMyProfile() ?: return@launch
            _state.update {
                it.copy(
                    username = profile.handle,
                    displayName = profile.displayName,
                    bio = profile.bio.orEmpty(),
                    avatarUrl = if (version == avatarVersion) profile.avatarUrl.toAbsoluteServerUrl() else it.avatarUrl,
                    followersCount = profile.followerCount,
                    followingCount = profile.followingCount,
                )
            }
        }
    }

    private fun saveText(displayName: String? = null, bio: String? = null) {
        if (_state.value.isSavingProfileText) return
        _state.update { it.copy(isSavingProfileText = true, profileTextError = null) }
        viewModelScope.launch {
            val result = updateProfileTextUseCase(displayName, bio)
            _state.update {
                when (result) {
                    is UpdateProfileTextUseCase.Result.Updated -> it.copy(
                        displayName = result.displayName,
                        bio = result.bio,
                        isSavingProfileText = false,
                        profileTextSavedToken = it.profileTextSavedToken + 1,
                    )
                    is UpdateProfileTextUseCase.Result.Failed ->
                        it.copy(isSavingProfileText = false, profileTextError = result.message)
                }
            }
        }
    }

    private fun changeAvatar(jpeg: ByteArray) {
        if (_state.value.isUpdatingAvatar) return
        avatarVersion++
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
                // 서버에서 받은 사진은 비운다 — 다른 계정으로 바뀌었는데 이전 사람 사진이 남지 않게 (서버 조회가 다시 채움)
                avatarUrl = null,
                avatarError = null,
                bio = "",
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
