package com.gentlelady.reborn.myprofile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
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
    private val currentUserId: CurrentUserIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MyProfileState())
    val state: StateFlow<MyProfileState> = _state.asStateFlow()

    /** 로그인한 계정이 바뀔 때마다 올린다 — 이전 계정에 대한 늦은 응답을 버린다 */
    private var accountGeneration = 0

    init {
        // 처음 + 로그인·로그아웃·계정 전환마다: 이전 계정 데이터를 지우고 다시 받는다
        viewModelScope.launch {
            currentUserId.changes.collect {
                accountGeneration++
                handleIntent(MyProfileIntent.LoadProfile)
            }
        }
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
            is MyProfileIntent.ClickViewAllFeeds,
            is MyProfileIntent.ClickFollowers,
            is MyProfileIntent.ClickFollowing -> { /* 화면 이동은 내비게이션에서 */ }
            is MyProfileIntent.ClickManagementMenu -> { /* 메뉴 분기 비즈니스 */ }
        }
    }

    private var profileJob: Job? = null
    /** 아바타를 바꾸기 시작할 때마다 올린다 — 그 전에 출발한 조회 응답이 새 사진을 옛 사진으로 덮지 않게 */
    private var avatarVersion = 0
    /** 닉네임·소개를 저장하기 시작할 때마다 올린다 — 같은 이유 */
    private var textVersion = 0

    /** 서버 프로필로 이름·핸들·사진·팔로워 수·내 글을 덮는다. 실패하면 목업 그대로 (내 글만 실패하면 글만 목업). */
    private fun loadServerProfile() {
        profileJob?.cancel() // 조회는 하나만 — 늦게 온 옛 응답이 덮지 않게
        val version = avatarVersion
        val text = textVersion
        val account = accountGeneration
        profileJob = viewModelScope.launch {
            val profile = repository.getMyProfile() ?: return@launch
            if (account != accountGeneration) return@launch
            _state.update {
                it.copy(
                    username = profile.handle,
                    displayName = if (text == textVersion) profile.displayName else it.displayName,
                    bio = if (text == textVersion) profile.bio.orEmpty() else it.bio,
                    avatarUrl = if (version == avatarVersion) profile.avatarUrl.toAbsoluteServerUrl() else it.avatarUrl,
                    // 서버 프로필이 오면 목업 사진은 치운다 — 사진이 없는 계정에 남의 목업 사진이 보이지 않게
                    profileImageUrl = null,
                    followersCount = profile.followerCount,
                    followingCount = profile.followingCount,
                    posthumousFeedCount = profile.posthumousPostCount,
                    myUserId = profile.id,
                    // 로그인한 사람의 화면에 목업 글을 남기지 않는다 (글 조회가 실패해도)
                    feeds = emptyList(),
                )
            }
            val posts = runCatching { repository.myPosts(profile.id, FEED_CARD_COUNT) }.getOrNull()
            if (posts != null && account == accountGeneration) _state.update { it.copy(feeds = posts) }
        }
    }

    private fun saveText(displayName: String? = null, bio: String? = null) {
        if (_state.value.isSavingProfileText) return
        textVersion++
        _state.update { it.copy(isSavingProfileText = true, profileTextError = null) }
        viewModelScope.launch {
            val result = updateProfileTextUseCase(displayName, bio)
            _state.update {
                when (result) {
                    is UpdateProfileTextUseCase.Result.Updated -> it.copy(
                        displayName = result.displayName,
                        bio = result.bio,
                        isSavingProfileText = false,
                        displayNameSavedToken = it.displayNameSavedToken + if (displayName != null) 1 else 0,
                        bioSavedToken = it.bioSavedToken + if (bio != null) 1 else 0,
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
                myUserId = null,
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

/** 프로필 피드 카드에 보여줄 최근 글 수 (전체는 "전체보기" → 내 프로필 화면) */
private const val FEED_CARD_COUNT = 10
