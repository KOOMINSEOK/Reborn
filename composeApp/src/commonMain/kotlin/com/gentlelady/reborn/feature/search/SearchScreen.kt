package com.gentlelady.reborn.feature.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.core.designsystem.PostCard.PostItem
import com.gentlelady.reborn.core.designsystem.MemorialCard.MemorialProfileItem
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.feature.search.components.SearchTopAppBar
import com.gentlelady.reborn.search.domain.mapper.toHomePost
import com.gentlelady.reborn.search.domain.mapper.toMemorialItem
import com.gentlelady.reborn.search.presentation.SearchIntent
import com.gentlelady.reborn.search.presentation.SearchState
import com.gentlelady.reborn.search.presentation.ItemType
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SearchScreen(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            SearchTopAppBar(
                query = state.query,
                onQueryChange = { onIntent(SearchIntent.UpdateQuery(it)) },
                currentTab = state.currentTab,
                onTabSelected = { onIntent(SearchIntent.ChangeTab(it)) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(state.results) { item ->
                when (item.type) {
                    ItemType.POST -> {
                        // Post 모델로 변환하여 PostItem 사용
                        PostItem(post = item.toHomePost())
                    }
                    ItemType.MEMORIAL -> {
                        // Memorial 모델로 변환하여 MemorialProfileItem 사용
                        MemorialProfileItem(
                            item = item.toMemorialItem(),
                            onVisitClick = { id -> onIntent(SearchIntent.ClickResultItem(id)) }
                        )
                    }
                }
                HorizontalDivider(color = RebornDividerGray, thickness = 1.dp)
            }
        }
    }
}

@Preview(showBackground = true, name = "Search Screen - Post Tab")
@Composable
private fun SearchScreenPostPreview() {
    MaterialTheme {
        Surface {
            // Post 탭 더미 데이터
            val mockState = SearchState(
                query = "김첨지",
                currentTab = 0,
                results = listOf(
                    com.gentlelady.reborn.search.presentation.SearchItem(
                        id = "1", title = "김첨지", subtitle = "나의 마지막 기록...", type = ItemType.POST
                    )
                )
            )
            SearchScreen(state = mockState, onIntent = {})
        }
    }
}

@Preview(showBackground = true, name = "Search Screen - Memorial Tab")
@Composable
private fun SearchScreenMemorialPreview() {
    MaterialTheme {
        Surface {
            // Memorial 탭 더미 데이터
            val mockState = SearchState(
                query = "소방관",
                currentTab = 2,
                results = listOf(
                    com.gentlelady.reborn.search.presentation.SearchItem(
                        id = "2", title = "박소방", subtitle = "소방관", type = ItemType.MEMORIAL
                    )
                )
            )
            SearchScreen(state = mockState, onIntent = {})
        }
    }
}