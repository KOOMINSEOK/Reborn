package com.gentlelady.reborn.feature.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gentlelady.reborn.core.designsystem.comments.CommentCountEffect
import com.gentlelady.reborn.core.designsystem.comments.CommentSheet
import com.gentlelady.reborn.core.network.InteractionTarget
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
    onIntent: (HomeIntent) -> Unit = {},
    onAddClick: () -> Unit = {},
    onAuthorClick: (String) -> Unit = {}
) {
    var commentPostId by remember { mutableStateOf<String?>(null) }
    CommentCountEffect { change ->
        if (change.target == InteractionTarget.POST) onIntent(HomeIntent.CommentCountChanged(change.parentId, change.delta))
    }

    Scaffold(
        containerColor = RebornBackground, // 하드코딩 제거: 시맨틱 테마 컬러 적용
        contentWindowInsets = WindowInsets(0.dp), // 바깥 MainScreen Scaffold가 이미 하단 인셋을 처리하므로 중복 방지
        topBar = {
            HomeTopAppBar(onAddClick = onAddClick)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(state.posts, key = { it.id }) { post ->
                PostItem(
                    post = post,
                    onLikeClick = { onIntent(HomeIntent.ToggleLike(post.id)) },
                    onCommentClick = { commentPostId = post.id },
                    onAuthorClick = onAuthorClick
                )
            }
        }
    }

    commentPostId?.let { postId ->
        CommentSheet(
            target = InteractionTarget.POST,
            parentId = postId,
            onDismiss = { commentPostId = null }
        )
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