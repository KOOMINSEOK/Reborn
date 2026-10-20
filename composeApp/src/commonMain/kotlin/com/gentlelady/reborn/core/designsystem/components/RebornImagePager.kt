package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gentlelady.reborn.core.theme.RebornWhite

/**
 * 서버 이미지 URL 목록을 그린다. 1장이면 그냥 이미지, 2장 이상이면 좌우로 넘기는 캐러셀 + 하단 점 표시.
 * 크기는 [modifier] 가 정한다 (호출부가 높이 지정).
 */
@Composable
fun RebornImagePager(
    urls: List<String>,
    modifier: Modifier = Modifier,
) {
    if (urls.size <= 1) {
        AsyncImage(
            model = urls.firstOrNull(),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
        return
    }

    val pagerState = rememberPagerState { urls.size }
    Box(modifier = modifier) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            AsyncImage(
                model = urls[page],
                contentDescription = "사진 ${page + 1}/${urls.size}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp),
        ) {
            repeat(urls.size) { index ->
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(RebornWhite.copy(alpha = if (index == pagerState.currentPage) 1f else 0.5f))
                )
            }
        }
    }
}
