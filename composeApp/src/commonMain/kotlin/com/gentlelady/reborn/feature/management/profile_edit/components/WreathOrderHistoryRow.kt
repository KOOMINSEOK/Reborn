package com.gentlelady.reborn.feature.management.profile_edit.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.feature.wreath_purchase.components.wreathTierInfo
import com.gentlelady.reborn.management.profile_edit.domain.model.WreathOrderHistoryItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun WreathOrderHistoryRow(
    item: WreathOrderHistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(wreathTierInfo(item.wreathTierCode).imageRes),
            contentDescription = item.memorialName,
            modifier = Modifier.size(56.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.memorialName,
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.wreathTypeLabel,
                color = RebornSlateGray,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.paymentDateLabel,
                color = RebornSlateGray,
                fontSize = 12.sp
            )
        }

        Text(
            text = item.priceLabel,
            color = RebornCobaltBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun WreathOrderHistoryRowPreview() {
    MaterialTheme {
        WreathOrderHistoryRow(
            item = WreathOrderHistoryItem(
                id = "1",
                memorialName = "故 이철수 님의 메모리얼",
                wreathTypeLabel = "스페셜형 화환",
                paymentDateLabel = "2026.07.28 결제완료",
                priceLabel = "4,900원",
                wreathTierCode = "special"
            ),
            onClick = {}
        )
    }
}
