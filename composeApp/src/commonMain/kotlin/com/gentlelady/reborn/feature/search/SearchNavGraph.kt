package com.gentlelady.reborn.feature.search

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.gentlelady.reborn.search.presentation.SearchIntent
import com.gentlelady.reborn.search.presentation.SearchState

/**
 * MainScreen의 NavHost 복잡도를 낮추기 위한 검색 피처 전용 네비게이션 목차 빌더
 */
fun NavGraphBuilder.searchNavGraph(
    navController: NavHostController,
    state: SearchState,
    onIntent: (SearchIntent) -> Unit
) {
    // BottomNavigationBar의 route명인 "search"와 일치시킵니다.
    composable(route = "search") {
        LaunchedEffect(Unit) { onIntent(SearchIntent.Refresh) }
        SearchScreen(
            state = state,
            onIntent = { intent ->
                when (intent) {
                    // 사람 → 프로필, 추모 페이지 → 그 페이지
                    is SearchIntent.ClickResultItem -> state.results.firstOrNull { it.id == intent.itemId }?.let { item ->
                        navController.navigate(if (item.hasMemorial) "memorial/other/${item.id}" else "user/${item.id}")
                    }
                    else -> onIntent(intent)
                }
            },
            onAuthorClick = { navController.navigate("user/$it") }
        )
    }
}
