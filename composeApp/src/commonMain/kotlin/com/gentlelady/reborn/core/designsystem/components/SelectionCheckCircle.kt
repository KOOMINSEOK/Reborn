package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDividerGray
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 선택 목록(작성하기 바텀시트, 대상 선택 등)에서 반복되는 선택 표시 원형 아이콘.
 * 선택됨 → 채워진 체크, 선택 안 됨 → 빈 원형 테두리.
 */
@Composable
fun SelectionCheckCircle(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    selectedTint: Color = RebornCobaltBlue,
    unselectedBorderColor: Color = RebornDividerGray
) {
    if (isSelected) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "선택됨",
            tint = selectedTint,
            modifier = modifier.size(size)
        )
    } else {
        androidx.compose.foundation.layout.Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(Color.White)
                .border(width = 1.5.dp, color = unselectedBorderColor, shape = CircleShape)
        )
    }
}

@Preview
@Composable
private fun SelectionCheckCirclePreview() {
    MaterialTheme {
        Surface {
            androidx.compose.foundation.layout.Row {
                SelectionCheckCircle(isSelected = true)
                SelectionCheckCircle(isSelected = false)
            }
        }
    }
}
