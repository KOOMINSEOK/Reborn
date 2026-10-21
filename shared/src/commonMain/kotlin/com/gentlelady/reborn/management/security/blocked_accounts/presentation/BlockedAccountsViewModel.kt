package com.gentlelady.reborn.management.security.blocked_accounts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.data.BlockedAccountsMockData
import com.gentlelady.reborn.userprofile.domain.GetBlockedUsersUseCase
import com.gentlelady.reborn.userprofile.domain.SetBlockUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 설정 > 차단한 계정. 로그인했으면 서버 목록, 둘러보기(로그인 안 함)면 목업.
 * 해제는 서버에 반영된 뒤 목록에서 뺀다 (실패하면 그대로 두고 안내).
 */
class BlockedAccountsViewModel(
    private val getBlockedUsers: GetBlockedUsersUseCase,
    private val setBlock: SetBlockUseCase,
    private val currentUserId: CurrentUserIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(BlockedAccountsState())
    val state: StateFlow<BlockedAccountsState> = _state.asStateFlow()

    private var loadJob: Job? = null
    /** 다시 받을 때마다 올린다 — 이전 조회·해제 응답이 새 목록을 덮지 않게 */
    private var generation = 0

    fun handleIntent(intent: BlockedAccountsIntent) {
        when (intent) {
            is BlockedAccountsIntent.LoadBlockedAccounts -> load()
            is BlockedAccountsIntent.ClickBack -> { /* 상위 네비게이션에서 처리 */ }
            is BlockedAccountsIntent.ClickUnblock -> _state.update { it.copy(unblockTargetId = intent.accountId) }
            is BlockedAccountsIntent.ConfirmUnblock -> confirmUnblock()
            is BlockedAccountsIntent.DismissUnblockConfirm -> _state.update { it.copy(unblockTargetId = null) }
            is BlockedAccountsIntent.MessageShown -> _state.update { it.copy(message = null) }
        }
    }

    private fun load() {
        val gen = ++generation
        loadJob?.cancel()
        if (currentUserId() == null) {
            _state.update { BlockedAccountsState(accounts = BlockedAccountsMockData.blockedAccounts, isLoading = false) }
            return
        }
        // 다시 들어올 땐 이전 목록을 보여 주면서 받는다 (깜빡임 없이)
        _state.update { it.copy(isLoading = it.accounts.isEmpty(), loadFailed = false, unblockTargetId = null) }
        loadJob = viewModelScope.launch {
            val result = runCatching { getBlockedUsers() }
            if (gen != generation) return@launch
            result
                .onSuccess { accounts -> _state.update { it.copy(accounts = accounts, isLoading = false, loadFailed = false) } }
                .onFailure { _state.update { it.copy(isLoading = false, loadFailed = true) } }
        }
    }

    private fun confirmUnblock() {
        val s = _state.value
        val id = s.unblockTargetId ?: return
        _state.update { it.copy(unblockTargetId = null) }
        if (id in s.unblockingIds) return
        if (currentUserId() == null) { // 둘러보기 목업
            _state.update { st -> st.copy(accounts = st.accounts.filterNot { it.id == id }) }
            return
        }
        val gen = generation
        _state.update { it.copy(unblockingIds = it.unblockingIds + id) }
        viewModelScope.launch {
            val result = runCatching { setBlock(id, false) }
            if (gen != generation) return@launch
            _state.update { st ->
                if (result.isSuccess) {
                    st.copy(
                        accounts = st.accounts.filterNot { it.id == id },
                        unblockingIds = st.unblockingIds - id,
                        unblockedToken = st.unblockedToken + 1,
                    )
                } else {
                    st.copy(unblockingIds = st.unblockingIds - id, message = "차단을 풀지 못했어요. 잠시 후 다시 시도해 주세요.")
                }
            }
        }
    }
}
