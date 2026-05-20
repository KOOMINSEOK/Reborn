package com.gentlelady.reborn.feature.memorial_swipe

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.img_memorial_bg_dummy
import com.gentlelady.reborn.feature.memorial_swipe.components.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MemorialSwipeScreen(
    state: MemorialSwipeState,
    onIntent: (MemorialSwipeIntent) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { state.memorialItems.size })

    Scaffold(
        containerColor = Color.Black
    ) { padding ->
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) { page ->
            val currentItem = state.memorialItems[page]

            Box(modifier = Modifier.fillMaxSize()) {
                // 외부 데이터 주입형 배경 렌더링 설계 (Stateless 분기)
                if (currentItem.backgroundImageUrl.isEmpty()) {
                    Image(
                        painter = painterResource(Res.drawable.img_memorial_bg_dummy),
                        contentDescription = "Memorial Immersive Default Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // 추후 확장: Coil AsyncImage 등을 활용한 서버 사이드 동적 바인딩 구역
                    Image(
                        painter = painterResource(Res.drawable.img_memorial_bg_dummy),
                        contentDescription = "Memorial Immersive Dynamic Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )

                MemorialTopBar(
                    musicItem = currentItem.currentMusic,
                    onHomeClick = { onIntent(MemorialSwipeIntent.NavigateHome) }
                )

                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(76.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MemorialProfileContent(currentItem)

                        Spacer(modifier = Modifier.height(16.dp))
                        MemorialSwipeIndicator()
                    }

                    MemorialBottomButtons(
                        memorialId = currentItem.id,
                        onFollowClick = { onIntent(MemorialSwipeIntent.FollowClick(it)) },
                        onShareClick = { onIntent(MemorialSwipeIntent.ShareClick(it)) },
                        onVisitPageClick = { onIntent(MemorialSwipeIntent.VisitPageClick(it)) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Immersive Memorial Screen Preview")
@Composable
fun MemorialSwipeScreenPreview() {
    val mockData = listOf(
        MemorialItem(
            id = "1",
            name = "김첨지",
            jobTitle = "소방관",
            location = "서울특별시",
            birthDate = "1987.03.02",
            deathDate = "2024.01.03",
            profileImageUrl = "",
            backgroundImageUrl = "",
            currentMusic = MusicItem("See You Again", "Wiz Khalifa ft. Charlie Puth", "")
        )
    )
    val mockState = MemorialSwipeState(memorialItems = mockData)
    MaterialTheme {
        MemorialSwipeScreen(state = mockState)
    }
}