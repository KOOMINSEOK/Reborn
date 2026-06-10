package com.gentlelady.reborn.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.home.domain.model.HomePost
import org.jetbrains.compose.ui.tooling.preview.Preview

// 하위 컴포넌트 패키지 임포트
import com.gentlelady.reborn.feature.home.components.HomeTopAppBar
// 대문자 패키지 규칙 유지 및 수정된 카드 컴포넌트 임포트
import com.gentlelady.reborn.core.designsystem.PostCard.PostItem
import com.gentlelady.reborn.core.theme.RebornBackground
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeState

@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit = {}
) {
    Scaffold(
        containerColor = RebornBackground, // 하드코딩 제거: 시맨틱 테마 컬러 적용
        topBar = {
            // 새로 개설한 콜백 파라미터에 HomeIntent를 매핑하여 단방향 데이터 흐름을 완성합니다.
            HomeTopAppBar(
                onMemorialClick = { onIntent(HomeIntent.ClickMemorialIcon) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(state.posts) { post ->
                PostItem(post = post)
                HorizontalDivider(color = RebornDividerGray, thickness = 1.dp)
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    // KMP Strict Rules 준수: String 공백 대신 변경된 도메인 스펙(DrawableResource?)에 맞춰 null 주입
    val mockPosts = listOf(
        HomePost(
            id = "1",
            authorName = "홍길동",
            authorProfileUrl = null,
            contentImageUrl = null,
            caption = "설날을 맞아 북한산으로 등산을 다녀왔습니다.",
            likes = 12,
            comments = 3,
            postedAt = "2026.02.18",
            isPosthumous = false
        ),
        HomePost(
            id = "2",
            authorName = "김첨지",
            authorProfileUrl = null,
            contentImageUrl = null,
            caption = "나의 마지막 기록이 여러분에게 닿기를...",
            likes = 150,
            comments = 45,
            postedAt = "2026.01.03",
            isPosthumous = true
        )
    )

    val mockState = HomeState(
        isLoading = false,
        posts = mockPosts,
        error = null
    )

    MaterialTheme {
        Surface(color = RebornBackground) {
            HomeScreen(state = mockState)
        }
    }
}