package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 여러 화면(히스토리, 방명록 사진첩 등)에서 재사용하는 1:1 정사각형 3열 사진 그리드.
 */
@Composable
fun ImageGridAlbum(
    images: List<GridImageSource>,
    onImageClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 3,
    spacing: Dp = 2.dp,
    emptyMessage: String = "등록된 사진이 없습니다."
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
                text = emptyMessage,
                color = RebornSlateGray,
                fontSize = 14.sp
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
            modifier = modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            itemsIndexed(images) { index, image ->
                ImageGridCell(
                    image = image,
                    onClick = { onImageClick(index) }
                )
            }
        }
    }
}

@Composable
private fun ImageGridCell(
    image: GridImageSource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(RebornDividerGray)
            .clickable(onClick = onClick)
    ) {
        when (image) {
            is GridImageSource.Resource -> {
                Image(
                    painter = painterResource(image.res),
                    contentDescription = "Grid Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is GridImageSource.Bitmap -> {
                Image(
                    bitmap = image.bitmap,
                    contentDescription = "Grid Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// Direct Injection 프리뷰 규칙 준수 (Provider/MockDataSource 금지)
@Preview
@Composable
private fun ImageGridAlbumEmptyPreview() {
    MaterialTheme {
        Surface {
            ImageGridAlbum(
                images = emptyList(),
                onImageClick = {}
            )
        }
    }
}
