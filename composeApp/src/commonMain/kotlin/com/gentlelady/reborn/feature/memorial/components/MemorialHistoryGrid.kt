// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/memorial/components/MemorialHistoryGrid.kt
package com.gentlelady.reborn.feature.memorial.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.data.MemorialMockData
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MemorialHistoryGrid(
    images: List<DrawableResource>,
    onImageClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "등록된 히스토리 사진이 없습니다.",
                color = RebornSlateGray,
                fontSize = 14.sp
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            itemsIndexed(images) { index, imageRes ->
                HistoryImageItem(
                    imageRes = imageRes,
                    onClick = { onImageClick(index) }
                )
            }
        }
    }
}

/**
 * 개별 그리드 이미지 셀 (지역 함수 중첩 금지 규칙에 따라 파일 레벨 분리)
 */
@Composable
private fun HistoryImageItem(
    imageRes: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f) // 1:1 정사각형 자르기
            .background(RebornDividerGray)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = "History Image",
            contentScale = ContentScale.Crop, // 이미지 꽉 차게 크롭
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Direct Injection 프리뷰 규칙 준수 (Provider/MockDataSource 금지)
@Preview
@Composable
private fun MemorialHistoryGridPreview() {
    MaterialTheme {
        Surface {
            MemorialHistoryGrid(
                images = MemorialMockData.historyImages,
                onImageClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun MemorialHistoryGridEmptyPreview() {
    MaterialTheme {
        Surface {
            MemorialHistoryGrid(
                images = emptyList(),
                onImageClick = {}
            )
        }
    }
}