package com.gentlelady.reborn.feature.search

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.search.presentation.SearchState
import com.gentlelady.reborn.search.presentation.SearchIntent

/**
 * MainScreen의 NavHost 복잡도를 낮추기 위한 검색 피처 전용 네비게이션 목차 빌더
 */
fun NavGraphBuilder.searchNavGraph(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit
) {
    // BottomNavigationBar의 route명인 "search"와 일치시킵니다.
    composable(route = "search") {
        SearchScreen(
            state = state,
            onIntent = onIntent
        )
    }
}