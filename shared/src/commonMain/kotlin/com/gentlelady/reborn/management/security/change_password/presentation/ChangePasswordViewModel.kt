package com.gentlelady.reborn.management.security.change_password.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChangePasswordViewModel : ViewModel() {

    private val _state = MutableStateFlow(ChangePasswordState())
    val state: StateFlow<ChangePasswordState> = _state.asStateFlow()

    fun handleIntent(intent: ChangePasswordIntent) {
        when (intent) {
            // 💡 이 화면을 나가면 입력값과 "재설정 이메일 발송" 배너를 초기화한다.
            // ViewModel이 화면 방문마다 새로 만들어지지 않고 앱 세션 동안 재사용되기 때문에, 여기서 명시적으로 리셋해야 다음 방문 때 깨끗한 상태로 보인다.
            is ChangePasswordIntent.ClickBack -> _state.update { ChangePasswordState() }
            is ChangePasswordIntent.CurrentPasswordChanged -> _state.update { it.copy(currentPassword = intent.value) }
            is ChangePasswordIntent.NewPasswordChanged -> _state.update { it.copy(newPassword = intent.value) }
            is ChangePasswordIntent.ConfirmPasswordChanged -> _state.update { it.copy(confirmPassword = intent.value) }
            is ChangePasswordIntent.ClickForgotPassword -> _state.update { it.copy(resetEmailSent = true) }
            is ChangePasswordIntent.ClickSubmit -> { /* TODO: 실제 비밀번호 변경 API 연동 */ }
        }
    }
}
