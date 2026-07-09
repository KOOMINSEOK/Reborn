package com.gentlelady.reborn.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.feature.profile.components.*
import com.gentlelady.reborn.profile.presentation.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfileScreen(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit
) {
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        // 시안의 스크롤에 따른 연속성(Continuity) 보장을 위해 LazyColumn 구조 채택
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding()) // 하단 탭바 영역 확보
        ) {
            // 1. 상단 백경, 프로필 아바타, 지표 정보 섹션
            item {
                ProfileHeaderSection(
                    state = state,
                    onEditBackgroundClick = { onIntent(ProfileIntent.ClickEditBackground) }
                )
            }

            // 2. 피드 캐로셀 섹션 (HorizontalPager 가로 스크롤 포함)
            item {
                ProfileFeedCarousel(
                    feeds = state.feeds,
                    onViewAllClick = { onIntent(ProfileIntent.ClickViewAllFeeds) }
                )
            }

            // 3. 하단 관리 Grid 섹션
            item {
                ProfileManagementGrid(
                    scheduledCount = state.scheduledFeedCount,
                    onMenuClick = { menuId -> onIntent(ProfileIntent.ClickManagementMenu(menuId)) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    // 프리뷰 안정성을 위해 가벼운 더미 데이터를 수동 주입 (Direct Injection)
    val previewState = ProfileState(
        username = "hong_gild",
        posthumousFeedCount = 5,
        followersCount = 248,
        followingCount = 91,
        scheduledFeedCount = 3,
        feeds = emptyList()
    )
    MaterialTheme {
        ProfileScreen(
            state = previewState,
            onIntent = {}
        )
    }
}