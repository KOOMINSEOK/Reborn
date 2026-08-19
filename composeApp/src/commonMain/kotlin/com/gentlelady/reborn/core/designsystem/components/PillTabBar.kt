package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * "저장됨" 화면의 게시물/히스토리/방명록처럼 여러 화면에서 재사용하는 캡슐형 탭바.
 * 선택된 탭은 파란 배경+흰 글씨, 나머지는 흰 배경+연한 테두리로 표시된다.
 */
@Composable
fun PillTabBar(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, label ->
            PillTabItem(
                label = label,
                isSelected = index == selectedIndex,
                onClick = { onTabSelect(index) }
            )
        }
    }
}

@Composable
private fun PillTabItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        color = if (isSelected) RebornCobaltBlue else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, RebornGridBorderGray)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else RebornSlateGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
        )
    }
}

@Preview
@Composable
private fun PillTabBarPreview() {
    MaterialTheme {
        PillTabBar(
            tabs = listOf("게시물", "히스토리", "방명록"),
            selectedIndex = 0,
            onTabSelect = {}
        )
    }
}
