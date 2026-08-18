package com.gentlelady.reborn.management.security.change_password.presentation

data class ChangePasswordState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val lastUpdatedLabel: String = "2026-05-10에 업데이트됨",
    val resetEmailSent: Boolean = false,
    val resetEmailAddress: String = "hongg@naver.com"
)

sealed interface ChangePasswordIntent {
    object ClickBack : ChangePasswordIntent
    data class CurrentPasswordChanged(val value: String) : ChangePasswordIntent
    data class NewPasswordChanged(val value: String) : ChangePasswordIntent
    data class ConfirmPasswordChanged(val value: String) : ChangePasswordIntent
    object ClickForgotPassword : ChangePasswordIntent
    object ClickSubmit : ChangePasswordIntent
}
