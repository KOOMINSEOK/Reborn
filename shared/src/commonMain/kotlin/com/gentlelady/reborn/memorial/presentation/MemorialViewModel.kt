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

    // 초기 상태로 Mock Data 바인딩
    private val _state = MutableStateFlow(MemorialMockData.myMemorialState)
    val state: StateFlow<MemorialState> = _state.asStateFlow()

    fun onIntent(intent: MemorialIntent) {
        when (intent) {
            // 💡 [추가] 뒤로가기 버튼 클릭 처리
            is MemorialIntent.ClickBack -> {
                _state.update { current ->
                    when {
                        current.isEditingProfile -> {
                            // 편집 중일 때 뒤로가기를 누르면 편집 모드만 끄고 내 메모리얼 화면 유지
                            current.copy(isEditingProfile = false)
                        }
                        current.isWritingHistory -> {
                            // 히스토리 작성 중일 때 뒤로가기를 누르면 작성 화면만 닫고 그리드로 복귀
                            current.copy(isWritingHistory = false)
                        }
                        current.selectedHistoryIndex != null -> {
                            // 히스토리 상세(추억 보기) 화면에서 뒤로가기를 누르면 그리드로 복귀
                            current.copy(selectedHistoryIndex = null)
                        }
                        else -> current
                    }
                }
            }

            // 1. 탭 전환 (히스토리 ↔ 방명록)
            is MemorialIntent.SelectTab -> {
                _state.update { it.copy(selectedTab = intent.tab) }
            }

            // 1-1. 히스토리 그리드 사진 클릭 → 상세(추억 보기) 화면 진입
            is MemorialIntent.ClickHistoryImage -> {
                _state.update { it.copy(selectedHistoryIndex = intent.index) }
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

                    val currentTimeMs = Clock.System.now().toEpochMilliseconds()

                    val newItem = MemorialGuestBookItem(
                        id = "gb_$currentTimeMs",
                        authorName = "나",
                        authorProfileUrl = null,
                        message = current.guestBookInputText,
                        timestamp = "방금 전"
                    )
                    current.copy(
                        guestBookMessages = current.guestBookMessages + newItem,
                        guestBookInputText = ""
                    )
                }
            }

            // 7. 히스토리 작성 화면 진입
            is MemorialIntent.ClickAddHistory -> {
                _state.update {
                    it.copy(
                        isWritingHistory = true,
                        historyWriteFormState = MemorialHistoryWriteFormState()
                    )
                }
            }

            // 8. 히스토리 작성 폼 캡션 입력
            is MemorialIntent.UpdateHistoryWriteCaption -> {
                _state.update { current ->
                    current.copy(historyWriteFormState = current.historyWriteFormState.copy(caption = intent.caption))
                }
            }

            // 8-1. 갤러리에서 사진 선택 완료
            is MemorialIntent.UpdateHistoryWriteImage -> {
                _state.update { current ->
                    current.copy(historyWriteFormState = current.historyWriteFormState.copy(imageBitmap = intent.imageBitmap))
                }
            }

            // 8-2. 선택한 사진 취소 (X 버튼) → 다시 사진 선택 박스로 복귀
            is MemorialIntent.ClickRemoveHistoryImage -> {
                _state.update { current ->
                    current.copy(historyWriteFormState = current.historyWriteFormState.copy(imageBitmap = null))
                }
            }

            // 9. 히스토리 게시 (사진이 선택되어 있을 때만 목록 맨 앞에 추가)
            is MemorialIntent.ClickPostHistory -> {
                _state.update { current ->
                    val selectedImageBitmap = current.historyWriteFormState.imageBitmap ?: return@update current

                    val newItem = MemorialHistoryItem(
                        id = "h_${Clock.System.now().toEpochMilliseconds()}",
                        imageBitmap = selectedImageBitmap,
                        authorName = "나",
                        date = "방금 전",
                        caption = current.historyWriteFormState.caption
                    )
                    current.copy(
                        historyItems = listOf(newItem) + current.historyItems,
                        isWritingHistory = false,
                        historyWriteFormState = MemorialHistoryWriteFormState()
                    )
                }
            }

            // 10. 히스토리 작성 취소
            is MemorialIntent.ClickCloseHistoryWrite -> {
                _state.update { it.copy(isWritingHistory = false) }
            }

            else -> { /* 이미지 클릭, 사진 선택 등 기타 네비게이션/플랫폼 연동 처리. ClickPurchaseWreath는 NavGraph에서 가로채 화환 라우트로 이동 */ }
        }
    }
}