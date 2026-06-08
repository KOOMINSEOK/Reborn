package com.gentlelady.reborn.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.home.domain.model.HomePost
import org.jetbrains.compose.ui.tooling.preview.Preview

// 하위 컴포넌트 패키지 임포트
import com.gentlelady.reborn.feature.home.components.HomeTopAppBar
import com.gentlelady.reborn.feature.home.components.PostItem
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeState
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter

@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit = {}
) {
    Scaffold(
        containerColor = Color.White,
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
                PostItem(post)
                HorizontalDivider(color = RebornDividerGray, thickness = 1.dp)
            }
        }
    }
}

@Preview(showBackground = true, name = "Home Screen Preview")
@Composable
fun HomeScreenPreview() {
    // 💡 Provider 없이 프리뷰 내부에서 안전한 하드코딩 데이터를 직접 생성
    val mockPosts = listOf(
        HomePost(
            id = "1",
            authorName = "홍길동",
            authorProfileUrl = "",
            contentImageUrl = "",
            caption = "설날을 맞아 북한산으로 등산을 다녀왔습니다.",
            postedAt = "",
            isPosthumous = false
        ),
        HomePost(
            id = "2",
            authorName = "김첨지",
            authorProfileUrl = "",
            contentImageUrl = "",
            caption = "나의 마지막 기록이 여러분에게 닿기를...",
            postedAt = "",
            isPosthumous = true
        )
    )

    val mockState = HomeState(
        isLoading = false,
        posts = mockPosts,
        error = null
    )

    MaterialTheme {
        // 💡 2. 직접 만든 상태를 주입합니다.
        HomeScreen(state = mockState)
    }
}