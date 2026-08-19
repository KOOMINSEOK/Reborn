package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
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
import com.gentlelady.reborn.core.theme.RebornDangerRed
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * "앱 설정 및 고객센터", "고객지원", "약관 및 탈퇴"처럼 카드 박스 없이 좌우 여백 없는 구분선으로만
 * 항목을 나누는 공용 리스트. 아이콘이 있는 [GroupedOptionList]와는 시각적으로 다른 화면이라 별도 컴포넌트로 분리했다.
 */
data class PlainOptionItem(
    val label: String,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun PlainOptionList(
    items: List<PlainOptionItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
            PlainOptionRow(item = item)
            if (index != items.lastIndex) {
                HorizontalDivider(thickness = 1.dp, color = RebornGridBorderGray)
            }
        }
    }
}

@Composable
private fun PlainOptionRow(
    item: PlainOptionItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { item.onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.label,
            color = if (item.isDestructive) RebornDangerRed else Color.Black,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = RebornSlateGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview
@Composable
private fun PlainOptionListPreview() {
    MaterialTheme {
        Surface {
            PlainOptionList(
                items = listOf(
                    PlainOptionItem(label = "이용약관", onClick = {}),
                    PlainOptionItem(label = "개인정보처리방침", onClick = {}),
                    PlainOptionItem(label = "앱 탈퇴 및 데이터 영구 삭제", isDestructive = true, onClick = {})
                )
            )
        }
    }
}
