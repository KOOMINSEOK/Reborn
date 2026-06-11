package com.gentlelady.reborn.feature.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.core.designsystem.PostCard.PostItem
import com.gentlelady.reborn.core.designsystem.MemorialCard.MemorialProfileItem
import com.gentlelady.reborn.core.theme.RebornBackground
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.feature.search.components.SearchTopAppBar
import com.gentlelady.reborn.search.presentation.SearchIntent
import com.gentlelady.reborn.search.presentation.SearchState
import com.gentlelady.reborn.search.presentation.SearchTab
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SearchScreen(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit
) {
    Scaffold(
        containerColor = RebornBackground, // 시맨틱 컬러 바인딩
        topBar = {
            SearchTopAppBar(
                query = state.query,
                onQueryChange = { onIntent(SearchIntent.UpdateQuery(it)) },
                currentTab = state.currentTab.index,
                onTabSelected = { index ->
                    // 인덱스를 기반으로 확장된 enum 탭 매핑 처리
                    val selectedTab = SearchTab.entries.firstOrNull { it.index == index } ?: SearchTab.LIKES
                    onIntent(SearchIntent.ChangeTab(selectedTab))
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 기획 변경 사항 반영: 탭 분기에 따른 전용 컴포넌트 출력 시스템
            when (state.currentTab) {
                SearchTab.LIKES, SearchTab.VIEWS -> {
                    // 1. Post 전용 렌더링 영역
                    items(state.postResults) { post ->
                        PostItem(post = post)
                        HorizontalDivider(color = RebornDividerGray, thickness = 1.dp)
                    }
                }
                SearchTab.FLOWERS -> {
                    // 2. Memorial 전용 렌더링 영역
                    items(state.memorialResults) { memorialItem ->
                        MemorialProfileItem(
                            item = memorialItem,
                            onVisitClick = { id -> onIntent(SearchIntent.ClickResultItem(id)) }
                        )
                        HorizontalDivider(color = RebornDividerGray, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SearchScreenPostTabPreview() {
    // Screen 1 기획서 시안 타겟 프리뷰
    val mockPostState = SearchState(
        query = "박지수",
        currentTab = SearchTab.LIKES,
        postResults = listOf(
            com.gentlelady.reborn.home.domain.model.HomePost(
                id = "p1",
                authorName = "박지수",
                authorProfileUrl = null,
                contentImageUrl = null,
                caption = "안녕하세요, 저는 다름아니라 최근 00사건에서 화두가 되었던...",
                likes = 3200,
                comments = 148,
                postedAt = "2026.06.11"
            )
        )
    )
    MaterialTheme {
        Surface {
            SearchScreen(state = mockPostState, onIntent = {})
        }
    }
}

@Preview
@Composable
private fun SearchScreenMemorialTabPreview() {
    // Screen 2 기획서 시안 타겟 프리뷰 (조화많은순)
    val mockMemorialState = SearchState(
        query = "",
        currentTab = SearchTab.FLOWERS,
        memorialResults = listOf(
            com.gentlelady.reborn.search.domain.entity.MemorialSearchItem(
                id = "m1",
                rank = 1,
                name = "이수진",
                birthDate = "1965",
                deathDate = "2023",
                location = "서울",
                flowerCount = "24.8k",
                profileImageUrl = null,
                isVerified = false
            ),
            com.gentlelady.reborn.search.domain.entity.MemorialSearchItem(
                id = "m2",
                rank = 2,
                name = "김민준",
                birthDate = "1978",
                deathDate = "2022",
                location = "부산",
                flowerCount = "18.3k",
                profileImageUrl = null,
                isVerified = true // 블루 체크 렌더링 활성화
            )
        )
    )
    MaterialTheme {
        Surface {
            SearchScreen(state = mockMemorialState, onIntent = {})
        }
    }
}