package com.gentlelady.reborn.feature.management.app_settings.customer_support.faq

import androidx.compose.foundation.layout.fillMaxSize
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
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.feature.management.app_settings.customer_support.faq.components.FaqItemRow
import com.gentlelady.reborn.management.app_settings.customer_support.faq.domain.model.FaqItem
import com.gentlelady.reborn.management.app_settings.customer_support.faq.presentation.FaqIntent
import com.gentlelady.reborn.management.app_settings.customer_support.faq.presentation.FaqState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FaqScreen(
    state: FaqState,
    onIntent: (FaqIntent) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "자주 묻는 질문", onBackClick = { onIntent(FaqIntent.ClickBack) })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            itemsIndexed(state.items, key = { _, item -> item.id }) { index, item ->
                FaqItemRow(
                    item = item,
                    isExpanded = item.id in state.expandedIds,
                    onClick = { onIntent(FaqIntent.ClickToggleFaq(item.id)) }
                )
                if (index != state.items.lastIndex) {
                    HorizontalDivider(thickness = 1.dp, color = RebornGridBorderGray)
                }
            }
        }
    }
}

@Preview
@Composable
private fun FaqScreenPreview() {
    val previewState = FaqState(
        items = listOf(
            FaqItem("1", "메모리얼이 뭔가요?", "메모리얼은 고인을 추모하고 기억하기 위해 마련하는 온라인 공간입니다."),
            FaqItem("2", "리마인드가 뭔가요?", "리마인드는 생전에 설정해 둔 메시지가 지정한 날짜에 전달되는 기능입니다.")
        ),
        expandedIds = setOf("1")
    )
    MaterialTheme {
        FaqScreen(state = previewState, onIntent = {})
    }
}
