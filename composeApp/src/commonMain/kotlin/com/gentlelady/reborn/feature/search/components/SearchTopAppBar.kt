package com.gentlelady.reborn.feature.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.core.theme.RebornBackgroundGray
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornUnselectedGray
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun SearchTopAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    showSortChips: Boolean // 이름 검색을 실행했을 때만 정렬 버튼 노출
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
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = RebornCobaltBlue)
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = RebornBackgroundGray,
                unfocusedContainerColor = RebornBackgroundGray,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )

        if (showSortChips) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SortChip(selected = currentTab == 0, label = "좋아요순", onClick = { onTabSelected(0) })
                SortChip(selected = currentTab == 1, label = "조회순", onClick = { onTabSelected(1) })
                SortChip(selected = currentTab == 2, label = "조화많은순", onClick = { onTabSelected(2) })
            }
        }
    }
}

@Composable
private fun SortChip(selected: Boolean, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(9999.dp),
        color = if (selected) RebornDeepBlue else Color.Transparent,
        border = if (!selected) BorderStroke(1.dp, RebornUnselectedGray) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) Color.White else RebornUnselectedGray
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchTopAppBarPreview() {
    MaterialTheme {
        Surface {
            SearchTopAppBar(
                query = "이수진",
                onQueryChange = {},
                currentTab = 2,
                onTabSelected = {},
                showSortChips = true
            )
        }
    }
}
