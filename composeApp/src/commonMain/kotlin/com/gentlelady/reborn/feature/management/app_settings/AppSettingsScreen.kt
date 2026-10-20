package com.gentlelady.reborn.feature.management.app_settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gentlelady.reborn.core.designsystem.components.PlainOptionItem
import com.gentlelady.reborn.core.designsystem.components.PlainOptionList
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornDangerRed
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * "앱 설정 및 고객센터" 진입 화면.
 */
@Composable
fun AppSettingsScreen(
    onBackClick: () -> Unit,
    onClickNotificationSettings: () -> Unit,
    onClickCustomerSupport: () -> Unit,
    onClickTerms: () -> Unit,
    onSignOut: () -> Unit = {}
) {
    var showSignOutConfirm by remember { mutableStateOf(false) }
    if (showSignOutConfirm) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirm = false },
            title = { Text("로그아웃할까요?") },
            text = { Text("이 기기에 저장된 로그인 정보가 지워져요. 다시 쓰려면 다시 로그인해야 해요.") },
            confirmButton = {
                TextButton(onClick = {
                    showSignOutConfirm = false
                    onSignOut()
                }) { Text("로그아웃", color = RebornDangerRed) }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirm = false }) { Text("취소") }
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "앱 설정 및 고객센터", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        PlainOptionList(
            items = listOf(
                PlainOptionItem(label = "알림(Push) 설정", onClick = onClickNotificationSettings),
                PlainOptionItem(label = "고객지원", onClick = onClickCustomerSupport),
                PlainOptionItem(label = "약관 및 탈퇴", onClick = onClickTerms),
                PlainOptionItem(label = "로그아웃", onClick = { showSignOutConfirm = true })
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        )
    }
}

@Preview
@Composable
private fun AppSettingsScreenPreview() {
    MaterialTheme {
        AppSettingsScreen(
            onBackClick = {},
            onClickNotificationSettings = {},
            onClickCustomerSupport = {},
            onClickTerms = {}
        )
    }
}
