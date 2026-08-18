package com.gentlelady.reborn.management.security.account_visibility.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AccountVisibilityViewModel : ViewModel() {

    private val _state = MutableStateFlow(AccountVisibilityState())
    val state: StateFlow<AccountVisibilityState> = _state.asStateFlow()

    init {
        handleIntent(AccountVisibilityIntent.LoadVisibility)
    }

    fun handleIntent(intent: AccountVisibilityIntent) {
        when (intent) {
            is AccountVisibilityIntent.LoadVisibility -> _state.update { it.copy(isLoading = false) }
            is AccountVisibilityIntent.ClickBack -> { /* 상위 네비게이션에서 처리 */ }
            is AccountVisibilityIntent.ClickSetPublic -> _state.update { it.copy(isPublic = true) }
            is AccountVisibilityIntent.ClickSetPrivate -> _state.update { it.copy(showSwitchToPrivateConfirm = true) }
            is AccountVisibilityIntent.ConfirmSwitchToPrivate -> _state.update {
                it.copy(isPublic = false, showSwitchToPrivateConfirm = false)
            }
            is AccountVisibilityIntent.DismissSwitchToPrivateConfirm -> _state.update {
                it.copy(showSwitchToPrivateConfirm = false)
            }
        }
    }
}
