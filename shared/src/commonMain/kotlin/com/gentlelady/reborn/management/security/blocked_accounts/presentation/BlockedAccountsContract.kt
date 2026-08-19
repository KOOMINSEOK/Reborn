package com.gentlelady.reborn.management.security.blocked_accounts.presentation

import com.gentlelady.reborn.management.security.blocked_accounts.domain.model.BlockedAccountItem

data class BlockedAccountsState(
    val accounts: List<BlockedAccountItem> = emptyList(),
    // 차단 해제 확인 다이얼로그가 뜬 계정 id (null이면 다이얼로그 없음)
    val unblockTargetId: String? = null,
    val isLoading: Boolean = true
)

sealed interface BlockedAccountsIntent {
    object LoadBlockedAccounts : BlockedAccountsIntent
    object ClickBack : BlockedAccountsIntent
    data class ClickUnblock(val accountId: String) : BlockedAccountsIntent
    object ConfirmUnblock : BlockedAccountsIntent
    object DismissUnblockConfirm : BlockedAccountsIntent
}
