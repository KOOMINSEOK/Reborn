package com.gentlelady.reborn.feature.management.app_settings.customer_support.faq.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornGridIconGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.management.app_settings.customer_support.faq.domain.model.FaqItem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun FaqItemRow(
    item: FaqItem,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "Q. ${item.question}",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = RebornSlateGray
            )
        }

        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp)
                    .background(color = RebornGridIconGray, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = if (item.answer.isBlank()) "A. 답변을 준비 중입니다." else "A. ${item.answer}",
                    color = RebornSlateGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Preview
@Composable
private fun FaqItemRowPreview() {
    MaterialTheme {
        Surface {
            FaqItemRow(
                item = FaqItem("1", "메모리얼이 뭔가요?", "메모리얼은 고인을 추모하고 기억하기 위해 마련하는 온라인 공간입니다."),
                isExpanded = true,
                onClick = {}
            )
        }
    }
}
