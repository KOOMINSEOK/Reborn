package com.gentlelady.reborn.feature.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.core.theme.RebornBackgroundGray // 추가된 임포트
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornUnselectedGray
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun SearchTopAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    currentTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("이름 혹은 ID 검색") },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = RebornBackgroundGray, // Color.kt 상수 사용
                unfocusedContainerColor = RebornBackgroundGray,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = currentTab == 0, label = "좋아요순", onClick = { onTabSelected(0) })
            FilterChip(selected = currentTab == 1, label = "조회순", onClick = { onTabSelected(1) })
            FilterChip(selected = currentTab == 2, label = "조회많은순", onClick = { onTabSelected(2) })
        }
    }
}

@Composable
private fun FilterChip(selected: Boolean, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(9999.dp),
        // 선택 시 RebornDeepBlue, 미선택 시 투명 배경 사용 [cite: 719, 746]
        color = if (selected) RebornDeepBlue else Color.Transparent,
        border = if (!selected) BorderStroke(1.dp, RebornUnselectedGray) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            // 선택 시 하얀색, 미선택 시 RebornUnselectedGray 사용 [cite: 719, 746]
            color = if (selected) Color.White else RebornUnselectedGray
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun SearchTopAppBarPreview() {
    MaterialTheme {
        Surface {
            // 더미 데이터를 직접 주입하여 안정적인 UI 렌더링 검증
            SearchTopAppBar(
                query = "김첨지",
                onQueryChange = {},
                currentTab = 0,
                onTabSelected = {}
            )
        }
    }
}