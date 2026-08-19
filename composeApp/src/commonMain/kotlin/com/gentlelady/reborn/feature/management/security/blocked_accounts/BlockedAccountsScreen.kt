package com.gentlelady.reborn.feature.management.security.blocked_accounts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
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
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "차단한 계정", onBackClick = { onIntent(BlockedAccountsIntent.ClickBack) })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            itemsIndexed(state.accounts, key = { _, account -> account.id }) { index, account ->
                BlockedAccountRow(
                    item = account,
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
