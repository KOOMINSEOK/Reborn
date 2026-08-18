package com.gentlelady.reborn.feature.management.security.account_visibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.feature.management.security.account_visibility.components.SwitchToPrivateConfirmDialog
import com.gentlelady.reborn.feature.management.security.account_visibility.components.VisibilityOptionCard
import com.gentlelady.reborn.management.security.account_visibility.presentation.AccountVisibilityIntent
import com.gentlelady.reborn.management.security.account_visibility.presentation.AccountVisibilityState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AccountVisibilityScreen(
    state: AccountVisibilityState,
    onIntent: (AccountVisibilityIntent) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(
                title = "계정 공개 범위",
                onBackClick = { onIntent(AccountVisibilityIntent.ClickBack) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VisibilityOptionCard(
                title = "공개 계정",
                description = "계정이 공개 상태인 경우 RE:BORN 계정이 없는 사람을 포함해 RE:BORN 안에서 모든 사람이 프로필과 게시물을 볼 수 있습니다.",
                isChecked = state.isPublic,
                onCheckedChange = { checked -> if (checked) onIntent(AccountVisibilityIntent.ClickSetPublic) }
            )
            VisibilityOptionCard(
                title = "비공개 계정",
                description = "계정이 비공개 상태인 경우 팔로워로 승인된 회원님의 팔로워만 회원님이 공유하는 사진이나 동영상을 볼 수 있습니다.",
                isChecked = !state.isPublic,
                onCheckedChange = { checked -> if (checked) onIntent(AccountVisibilityIntent.ClickSetPrivate) }
            )
        }

        if (state.showSwitchToPrivateConfirm) {
            SwitchToPrivateConfirmDialog(
                onConfirm = { onIntent(AccountVisibilityIntent.ConfirmSwitchToPrivate) },
                onDismiss = { onIntent(AccountVisibilityIntent.DismissSwitchToPrivateConfirm) }
            )
        }
    }
}

@Preview
@Composable
private fun AccountVisibilityScreenPublicPreview() {
    MaterialTheme {
        AccountVisibilityScreen(
            state = AccountVisibilityState(isPublic = true),
            onIntent = {}
        )
    }
}

@Preview
@Composable
private fun AccountVisibilityScreenConfirmPreview() {
    MaterialTheme {
        AccountVisibilityScreen(
            state = AccountVisibilityState(isPublic = true, showSwitchToPrivateConfirm = true),
            onIntent = {}
        )
    }
}
