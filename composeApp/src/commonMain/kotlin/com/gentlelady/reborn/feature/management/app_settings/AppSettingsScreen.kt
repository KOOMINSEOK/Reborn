package com.gentlelady.reborn.feature.management.app_settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gentlelady.reborn.core.designsystem.components.PlainOptionItem
import com.gentlelady.reborn.core.designsystem.components.PlainOptionList
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * "앱 설정 및 고객센터" 진입 화면.
 */
@Composable
fun AppSettingsScreen(
    onBackClick: () -> Unit,
    onClickNotificationSettings: () -> Unit,
    onClickCustomerSupport: () -> Unit,
    onClickTerms: () -> Unit
) {
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
                PlainOptionItem(label = "약관 및 탈퇴", onClick = onClickTerms)
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
