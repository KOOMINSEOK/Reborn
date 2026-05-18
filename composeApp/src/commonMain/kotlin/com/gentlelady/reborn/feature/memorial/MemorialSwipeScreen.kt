package com.gentlelady.reborn.feature.memorial

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
// 하위 컴포넌트들 정상 Import
import com.gentlelady.reborn.feature.memorial.components.*
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

            // 각 페이지의 루트 컨테이너 (BoxScope 제공)
            Box(modifier = Modifier.fillMaxSize()) {

                // A. 전체 배경 이미지 및 어두운 딤(Dim) 처리 [cite: 5]
                Box(modifier = Modifier.fillMaxSize().background(Color.Gray)) {
                    // KMP 동적 에셋 규칙에 따른 플레이스홀더 [cite: 5]
                    Text("BG Image", color = Color.White, modifier = Modifier.align(Alignment.Center))
                }
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

                // B. [수정] 상단 위젯 영역을 Box의 직계 자식으로 이동!
                // 이렇게 하면 BoxScope 수신 객체가 명확해져 컴파일 에러가 해결되고,
                // 시안처럼 배경 위에 상단 바가 올바르게 플로팅(Overlay)됩니다.
                MemorialTopBar(
                    musicItem = currentItem.currentMusic,
                    onHomeClick = { onIntent(MemorialSwipeIntent.NavigateHome) }
                )

                // C. 중앙 프로필 정보 및 하단 버튼들을 세로로 배치할 레이아웃
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 상단 플로팅 바와 콘텐츠가 겹치지 않도록 안전 마진(Spacer) 확보
                    Spacer(modifier = Modifier.height(72.dp))

                    // 중앙 프로필 영역 (내부 weight(1f)로 남은 상하 공간을 균등하게 차지)
                    MemorialProfileContent(currentItem)

                    // 세로 스와이프 안내 가이드 인디케이터
                    MemorialSwipeIndicator()

                    // 하단 버튼 그룹들 (팔로우, 공유하기, 페이지 이동)
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

@Preview(showBackground = true, name = "Memorial Swipe Screen Fixed Preview")
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