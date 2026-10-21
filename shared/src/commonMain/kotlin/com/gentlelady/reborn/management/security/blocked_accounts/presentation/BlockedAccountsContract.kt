package com.gentlelady.reborn.management.security.blocked_accounts.presentation

import com.gentlelady.reborn.management.security.blocked_accounts.domain.model.BlockedAccountItem

data class BlockedAccountsState(
    val accounts: List<BlockedAccountItem> = emptyList(),
    // 차단 해제 확인 다이얼로그가 뜬 계정 id (null이면 다이얼로그 없음)
    val unblockTargetId: String? = null,
    val isLoading: Boolean = true,
    val loadFailed: Boolean = false,
    /** 해제 요청 중인 계정 (버튼을 막는다) */
    val unblockingIds: Set<String> = emptySet(),
    /** 한 번 보여주고 지우는 안내 */
    val message: String? = null,
    /** 서버에서 차단을 풀 때마다 올린다 → 홈 피드가 다시 받는다 (그 사람 글이 다시 보이게) */
    val unblockedToken: Int = 0,
)

sealed interface BlockedAccountsIntent {
    /** 화면에 들어올 때마다 — 다른 화면에서 차단했을 수 있다 */
    object LoadBlockedAccounts : BlockedAccountsIntent
    object ClickBack : BlockedAccountsIntent
    data class ClickUnblock(val accountId: String) : BlockedAccountsIntent
    object ConfirmUnblock : BlockedAccountsIntent
    object DismissUnblockConfirm : BlockedAccountsIntent
    object MessageShown : BlockedAccountsIntent
}
