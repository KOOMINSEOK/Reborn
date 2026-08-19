package com.gentlelady.reborn.feature.management.app_settings.terms

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

@Composable
fun TermsScreen(
    onBackClick: () -> Unit,
    onClickTermsOfUse: () -> Unit,
    onClickPrivacyPolicy: () -> Unit,
    onClickWithdrawal: () -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "약관 및 탈퇴", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        PlainOptionList(
            items = listOf(
                PlainOptionItem(label = "이용약관", onClick = onClickTermsOfUse),
                PlainOptionItem(label = "개인정보처리방침", onClick = onClickPrivacyPolicy),
                PlainOptionItem(label = "앱 탈퇴 및 데이터 영구 삭제", isDestructive = true, onClick = onClickWithdrawal)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        )
    }
}

@Preview
@Composable
private fun TermsScreenPreview() {
    MaterialTheme {
        TermsScreen(onBackClick = {}, onClickTermsOfUse = {}, onClickPrivacyPolicy = {}, onClickWithdrawal = {})
    }
}
