package com.gentlelady.reborn.feature.management.app_settings.customer_support

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
fun CustomerSupportScreen(
    onBackClick: () -> Unit,
    onClickInquiry: () -> Unit,
    onClickFaq: () -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "고객지원", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        PlainOptionList(
            items = listOf(
                PlainOptionItem(label = "1:1 문의", onClick = onClickInquiry),
                PlainOptionItem(label = "자주 묻는 질문(FAQ)", onClick = onClickFaq)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        )
    }
}

@Preview
@Composable
private fun CustomerSupportScreenPreview() {
    MaterialTheme {
        CustomerSupportScreen(onBackClick = {}, onClickInquiry = {}, onClickFaq = {})
    }
}
