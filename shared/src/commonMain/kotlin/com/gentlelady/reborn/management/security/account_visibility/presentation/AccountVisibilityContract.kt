package com.gentlelady.reborn.management.security.account_visibility.presentation

data class AccountVisibilityState(
    val isPublic: Boolean = true,
    // 비공개 계정으로 전환하기 전 노출하는 확인 다이얼로그 표시 여부
    val showSwitchToPrivateConfirm: Boolean = false,
    val isLoading: Boolean = true
)

sealed interface AccountVisibilityIntent {
    object LoadVisibility : AccountVisibilityIntent
    object ClickBack : AccountVisibilityIntent
    // 공개 전환은 즉시 적용, 비공개 전환은 확인 다이얼로그를 먼저 띄운다.
    object ClickSetPublic : AccountVisibilityIntent
    object ClickSetPrivate : AccountVisibilityIntent
    object ConfirmSwitchToPrivate : AccountVisibilityIntent
    object DismissSwitchToPrivateConfirm : AccountVisibilityIntent
}
