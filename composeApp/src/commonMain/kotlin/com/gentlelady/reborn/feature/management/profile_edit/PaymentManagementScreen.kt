package com.gentlelady.reborn.feature.management.profile_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.gentlelady.reborn.feature.management.profile_edit.components.WreathOrderHistoryRow
import com.gentlelady.reborn.management.profile_edit.domain.model.WreathOrderHistoryItem
import com.gentlelady.reborn.management.profile_edit.presentation.PaymentHistoryIntent
import com.gentlelady.reborn.management.profile_edit.presentation.PaymentHistoryState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PaymentManagementScreen(
    state: PaymentHistoryState,
    onIntent: (PaymentHistoryIntent) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(
                title = "보낸 화환 내역",
                onBackClick = { onIntent(PaymentHistoryIntent.ClickBack) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            itemsIndexed(state.orders, key = { _, order -> order.id }) { index, order ->
                WreathOrderHistoryRow(
                    item = order,
                    onClick = { onIntent(PaymentHistoryIntent.ClickOrder(order.id)) }
                )
                if (index != state.orders.lastIndex) {
                    HorizontalDivider(thickness = 1.dp, color = RebornGridBorderGray)
                }
            }
        }
    }
}

@Preview
@Composable
private fun PaymentManagementScreenPreview() {
    val previewState = PaymentHistoryState(
        orders = listOf(
            WreathOrderHistoryItem("1", "故 이철수 님의 메모리얼", "스페셜형 화환", "2026.07.28 결제완료", "4,900원", "special")
        )
    )
    MaterialTheme {
        PaymentManagementScreen(state = previewState, onIntent = {})
    }
}
