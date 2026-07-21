// shared/src/commonMain/kotlin/com/gentlelady/reborn/memorial/presentation/MemorialViewModel.kt
package com.gentlelady.reborn.memorial.presentation

import androidx.lifecycle.ViewModel
import com.gentlelady.reborn.data.MemorialMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock

class MemorialViewModel : ViewModel() {

    // 초기 상태로 Mock Data 바인딩 (Owner View)
    private val _state = MutableStateFlow(
        MemorialMockData.myMemorialState.copy(
            ownerType = MemorialOwnerType.MY_MEMORIAL
        )
    )
    val state: StateFlow<MemorialState> = _state.asStateFlow()

    fun onIntent(intent: MemorialIntent) {
        when (intent) {
            // 1. 탭 전환 (히스토리 ↔ 방명록)
            is MemorialIntent.SelectTab -> {
                _state.update { it.copy(selectedTab = intent.tab) }
            }

            // 2. 프로필 편집 화면 진입
            is MemorialIntent.ClickEditProfile -> {
                _state.update { current ->
                    current.copy(
                        isEditingProfile = true,
                        editFormState = EditProfileFormState(
                            name = current.profile.name,
                            handle = current.profile.handle,
                            bio = current.profile.bio,
                            profileImageRes = current.profile.profileImageRes
                        )
                    )
                }
            }

            // 3. 프로필 편집 폼 입력 업데이트
            is MemorialIntent.UpdateEditName -> {
                _state.update { current ->
                    current.copy(editFormState = current.editFormState.copy(name = intent.name))
                }
            }
            is MemorialIntent.UpdateEditHandle -> {
                _state.update { current ->
                    current.copy(editFormState = current.editFormState.copy(handle = intent.handle))
                }
            }
            is MemorialIntent.UpdateEditBio -> {
                _state.update { current ->
                    current.copy(editFormState = current.editFormState.copy(bio = intent.bio))
                }
            }

            // 4. 프로필 편집 저장
            is MemorialIntent.ClickSaveProfile -> {
                _state.update { current ->
                    val updatedForm = current.editFormState
                    current.copy(
                        isEditingProfile = false,
                        profile = current.profile.copy(
                            name = updatedForm.name,
                            handle = updatedForm.handle,
                            bio = updatedForm.bio,
                            profileImageRes = updatedForm.profileImageRes
                        )
                    )
                }
            }

            // 5. 프로필 편집 취소 (닫기 버튼)
            is MemorialIntent.ClickCloseEditProfile -> {
                _state.update { it.copy(isEditingProfile = false) }
            }

            // 6. 방명록 입력 및 작성 제출
            is MemorialIntent.UpdateGuestBookInput -> {
                _state.update { it.copy(guestBookInputText = intent.text) }
            }
            is MemorialIntent.SubmitGuestBook -> {
                _state.update { current ->
                    if (current.guestBookInputText.isBlank()) return@update current

                    val currentTimeMs = Clock.System.now().toEpochMilliseconds() // 💡 멀티플랫폼 타임스탬프

                    val newItem = MemorialGuestBookItem(
                        id = "gb_$currentTimeMs",
                        authorName = "나",
                        authorProfileUrl = null,
                        message = current.guestBookInputText,
                        timestamp = "방금 전"
                    )
                    current.copy(
                        guestBookMessages = listOf(newItem) + current.guestBookMessages,
                        guestBookInputText = ""
                    )
                }
            }

            else -> { /* 뒤로가기, 이미지 클릭 등 네비게이션 처리 */ }
        }
    }
}