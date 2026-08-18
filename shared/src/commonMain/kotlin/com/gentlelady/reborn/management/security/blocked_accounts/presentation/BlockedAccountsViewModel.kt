package com.gentlelady.reborn.management.security.blocked_accounts.presentation

import androidx.lifecycle.ViewModel
import com.gentlelady.reborn.data.BlockedAccountsMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BlockedAccountsViewModel : ViewModel() {

    private val _state = MutableStateFlow(BlockedAccountsState())
    val state: StateFlow<BlockedAccountsState> = _state.asStateFlow()

    init {
        handleIntent(BlockedAccountsIntent.LoadBlockedAccounts)
    }

    fun handleIntent(intent: BlockedAccountsIntent) {
        when (intent) {
            is BlockedAccountsIntent.LoadBlockedAccounts -> _state.update {
                it.copy(accounts = BlockedAccountsMockData.blockedAccounts, isLoading = false)
            }
            is BlockedAccountsIntent.ClickBack -> { /* 상위 네비게이션에서 처리 */ }
            is BlockedAccountsIntent.ClickUnblock -> _state.update { it.copy(unblockTargetId = intent.accountId) }
            is BlockedAccountsIntent.ConfirmUnblock -> _state.update { s ->
                s.copy(
                    accounts = s.accounts.filterNot { it.id == s.unblockTargetId },
                    unblockTargetId = null
                )
            }
            is BlockedAccountsIntent.DismissUnblockConfirm -> _state.update { it.copy(unblockTargetId = null) }
        }
    }
}
