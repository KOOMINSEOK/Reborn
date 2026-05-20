package com.gentlelady.reborn.feature.memorial

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image // 이미지 컴포넌트 임포트
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale // 화면 크롭 정렬을 위한 임포트
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.Res // KMP 리소스 루트 객체
import com.gentlelady.reborn.img_memorial_bg_dummy // 추가한 배경 이미지 자동 생성 파일 임포트
import com.gentlelady.reborn.feature.memorial_swipe.components.*
import com.gentlelady.reborn.feature.memorial_swipe.MemorialItem
import com.gentlelady.reborn.feature.memorial_swipe.MemorialSwipeIntent
import com.gentlelady.reborn.feature.memorial_swipe.MemorialSwipeState
import com.gentlelady.reborn.feature.memorial_swipe.MusicItem
import com.gentlelady.reborn.feature.memorial_swipe.components.MemorialBottomButtons
import com.gentlelady.reborn.feature.memorial_swipe.components.MemorialSwipeIndicator
import com.gentlelady.reborn.feature.memorial_swipe.components.MemorialTopBar
import org.jetbrains.compose.resources.painterResource // KMP 리소스 로더 [cite: 77]
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MemorialSwipeScreen(
    state: MemorialSwipeState,
    onIntent: (MemorialSwipeIntent) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { state.memorialItems.size })

    Scaffold(
        containerColor = Color.Black // 바텀바가 없는 순수 몰입형 컨테이너 [cite: 85]
    ) { padding ->
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) { page ->
            val currentItem = state.memorialItems[page]

            Box(modifier = Modifier.fillMaxSize()) {

                // A. [수정] 임시 텍스트 플레이스홀더를 제거하고, 실제 드로어블 이미지를 배경으로 바인딩 [cite: 5, 69]
                // ContentScale.Crop을 사용하여 어떤 디바이스 화면 비율에서도 꽉 차게 렌더링합니다.
                Image(
                    painter = painterResource(Res.drawable.img_memorial_bg_dummy), // KMP 에셋 리소스 참조 [cite: 13]
                    contentDescription = "Memorial Immersive Background",
                    contentScale = ContentScale.Crop, // 비율을 유지하며 화면을 가득 채움
                    modifier = Modifier.fillMaxSize()
                )

                // B. 배경 이미지 위에 깔리는 어두운 딤(Dim) 레이어 [cite: 69, 86]
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)) // 시안 고유의 은은한 어둠 연출
                )

                // C. 상단 레이아웃 플로팅 컴포넌트 (BoxScope 수신 객체 구조 유지) [cite: 68]
                MemorialTopBar(
                    musicItem = currentItem.currentMusic,
                    onHomeClick = { onIntent(MemorialSwipeIntent.NavigateHome) }
                )

                // D. 콘텐츠 영역 적층 레이아웃
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(76.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f) // 하단 버튼을 최하단으로 밀어내고 본인은 중앙 영역 확보
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center, // 내부 아이템들을 수직 정중앙 정렬
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MemorialProfileContent(currentItem)

                        // 이제 프로필 정보 바로 밑에 쫀쫀하게 밀착하여 펄스 애니메이션이 표현됩니다.
                        Spacer(modifier = Modifier.height(16.dp)) // 시안 스페이싱 가이드 확보
                        MemorialSwipeIndicator()
                    }

                    // 하단 팔로우, 공유하기 및 고스트 테두리 스타일의 페이지 이동 버튼 [cite: 87, 103]
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