package com.gentlelady.reborn.feature.management.security.blocked_accounts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornSlateGray
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.feature.management.security.blocked_accounts.components.BlockedAccountRow
import com.gentlelady.reborn.feature.management.security.blocked_accounts.components.UnblockConfirmDialog
import com.gentlelady.reborn.img_memorial_profile_dummy
import com.gentlelady.reborn.management.security.blocked_accounts.domain.model.BlockedAccountItem
import com.gentlelady.reborn.management.security.blocked_accounts.presentation.BlockedAccountsIntent
import com.gentlelady.reborn.management.security.blocked_accounts.presentation.BlockedAccountsState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BlockedAccountsScreen(
    state: BlockedAccountsState,
    onIntent: (BlockedAccountsIntent) -> Unit
) {
    // 들어올 때마다 서버 목록 (프로필에서 방금 차단했을 수 있다)
    LaunchedEffect(Unit) { onIntent(BlockedAccountsIntent.LoadBlockedAccounts) }
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            onIntent(BlockedAccountsIntent.MessageShown)
        }
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            RebornBackTopAppBar(title = "차단한 계정", onBackClick = { onIntent(BlockedAccountsIntent.ClickBack) })
        }
    ) { paddingValues ->
        val note = when {
            state.isLoading -> null
            state.loadFailed -> "목록을 불러오지 못했어요."
            state.accounts.isEmpty() -> "차단한 계정이 없어요."
            else -> null
        }
        if (state.isLoading || note != null) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(note!!, color = RebornSlateGray, fontSize = 14.sp)
                        if (state.loadFailed) {
                            TextButton(onClick = { onIntent(BlockedAccountsIntent.LoadBlockedAccounts) }) { Text("다시 시도") }
                        }
                    }
                }
            }
        } else LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            itemsIndexed(state.accounts, key = { _, account -> account.id }) { index, account ->
                BlockedAccountRow(
                    item = account,
                    unblocking = account.id in state.unblockingIds,
                    onClickUnblock = { onIntent(BlockedAccountsIntent.ClickUnblock(account.id)) }
                )
                if (index != state.accounts.lastIndex) {
                    HorizontalDivider(thickness = 1.dp, color = RebornGridBorderGray)
                }
            }
        }

        val target = state.accounts.find { it.id == state.unblockTargetId }
        if (target != null) {
            UnblockConfirmDialog(
                target = target,
                onConfirm = { onIntent(BlockedAccountsIntent.ConfirmUnblock) },
                onDismiss = { onIntent(BlockedAccountsIntent.DismissUnblockConfirm) }
            )
        }
    }
}

@Preview
@Composable
private fun BlockedAccountsScreenPreview() {
    val previewState = BlockedAccountsState(
        accounts = listOf(
            BlockedAccountItem("1", "김민수", "min_su_99", Res.drawable.img_memorial_profile_dummy),
            BlockedAccountItem("2", "이영희", "younghee_lee", Res.drawable.img_memorial_profile_dummy)
        )
    )
    MaterialTheme {
        BlockedAccountsScreen(state = previewState, onIntent = {})
    }
}

@Preview
@Composable
private fun BlockedAccountsScreenConfirmPreview() {
    val previewState = BlockedAccountsState(
        accounts = listOf(
            BlockedAccountItem("1", "김민수", "min_su_99", Res.drawable.img_memorial_profile_dummy)
        ),
        unblockTargetId = "1"
    )
    MaterialTheme {
        BlockedAccountsScreen(state = previewState, onIntent = {})
    }
}
