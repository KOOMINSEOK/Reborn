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
            is ChangePasswordIntent.ClickBack -> { /* 상위 네비게이션에서 처리 */ }
            is ChangePasswordIntent.CurrentPasswordChanged -> _state.update { it.copy(currentPassword = intent.value) }
            is ChangePasswordIntent.NewPasswordChanged -> _state.update { it.copy(newPassword = intent.value) }
            is ChangePasswordIntent.ConfirmPasswordChanged -> _state.update { it.copy(confirmPassword = intent.value) }
            is ChangePasswordIntent.ClickForgotPassword -> _state.update { it.copy(resetEmailSent = true) }
            is ChangePasswordIntent.ClickSubmit -> { /* TODO: 실제 비밀번호 변경 API 연동 */ }
        }
    }
}
