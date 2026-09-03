package com.gentlelady.reborn.feature.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.core.designsystem.MemorialCard.MemorialProfileItem
import com.gentlelady.reborn.core.designsystem.PostCard.PostItem
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
        containerColor = RebornBackground,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            SearchTopAppBar(
                query = state.query,
                onQueryChange = { onIntent(SearchIntent.UpdateQuery(it)) },
                currentTab = state.currentTab.index,
                onTabSelected = { index ->
                    val selectedTab = SearchTab.entries.firstOrNull { it.index == index } ?: SearchTab.LIKES
                    onIntent(SearchIntent.ChangeTab(selectedTab))
                },
                showSortChips = state.isSearching
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isSearching) {
                // 이름 검색 결과: 사용자/메모리얼 카드 (currentTab 기준 정렬)
                items(state.results, key = { it.id }) { item ->
                    MemorialProfileItem(
                        item = item,
                        onVisitClick = { id -> onIntent(SearchIntent.ClickResultItem(id)) }
                    )
                }
            } else {
                // 검색 전 기본 화면: 무작위 게시글 피드
                items(state.randomFeed, key = { it.id }) { post ->
                    PostItem(post = post)
                    HorizontalDivider(color = RebornDividerGray, thickness = 1.dp)
                }
            }
        }
    }
}

@Preview
@Composable
private fun SearchScreenDefaultFeedPreview() {
    val mockState = SearchState(
        query = "",
        randomFeed = listOf(
            com.gentlelady.reborn.home.domain.model.HomePost(
                id = "p1",
                authorName = "박지수",
                authorProfileUrl = null,
                contentImageUrl = null,
                caption = "햇살이 부서지는 숲길을 걷고 오늘의 평안을 기록합니다.",
                likes = 1800,
                comments = 214,
                postedAt = "2026.06.11"
            )
        )
    )
    MaterialTheme { Surface { SearchScreen(state = mockState, onIntent = {}) } }
}

@Preview
@Composable
private fun SearchScreenResultsPreview() {
    val mockState = SearchState(
        query = "이수진",
        currentTab = SearchTab.FLOWERS,
        results = listOf(
            com.gentlelady.reborn.search.domain.entity.MemorialSearchItem(
                id = "m1", rank = 1, name = "이수진", birthDate = "1965", deathDate = "2023",
                location = "서울", flowerCount = "24.8k", profileImageUrl = null,
                isDeceased = false, hasProfile = true, hasMemorial = true
            ),
            com.gentlelady.reborn.search.domain.entity.MemorialSearchItem(
                id = "m2", rank = 2, name = "이수진", birthDate = "1988", deathDate = "-",
                location = "부산", flowerCount = "11.2k", profileImageUrl = null,
                isDeceased = false, hasProfile = true, hasMemorial = false
            ),
            com.gentlelady.reborn.search.domain.entity.MemorialSearchItem(
                id = "m3", rank = 3, name = "이수진", birthDate = "1951", deathDate = "2019",
                location = "인천", flowerCount = "8.6k", profileImageUrl = null,
                isDeceased = true, hasProfile = false, hasMemorial = true
            )
        )
    )
    MaterialTheme { Surface { SearchScreen(state = mockState, onIntent = {}) } }
}
