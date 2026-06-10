package com.gentlelady.reborn.feature.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.search.presentation.SearchState  // 1. 검색 상태 임포트
import com.gentlelady.reborn.search.presentation.SearchIntent // 2. 검색 인텐트 임포트

/**
 * App.kt에서 목차로 호출하는 메인 전체 플로우 네비게이션 그래프
 */
fun NavGraphBuilder.mainNavGraph(
    navController: NavController,
    homeState: HomeState,
    onHomeIntent: (HomeIntent) -> Unit,
    searchState: SearchState,              // 3. 외부(App.kt 등)로부터 검색 상태 주입 받음
    onSearchIntent: (SearchIntent) -> Unit  // 4. 외부로부터 검색 인텐트 핸들러 주입 받음
) {
    composable("main_flow") {
        MainScreen(
            homeState = homeState,
            onHomeIntent = { intent ->
                when (intent) {
                    // 규칙 준수: 화면 완전 몰입형 전환 로직은 오직 UI 레이어(Graph)단에서 낚아채서 처리
                    is HomeIntent.ClickMemorialIcon -> {
                        navController.navigate("memorial_swipe")
                    }
                    else -> onHomeIntent(intent)
                }
            },
            searchState = searchState,             // 5. MainScreen으로 검색 상태 전달 완료
            onSearchIntent = { intent ->
                when (intent) {
                    // 혹시 검색 결과 리스트 아이템 클릭 시 화면 전환 등 UI 전용 로직 제어가 필요하다면
                    // 여기서 when문으로 가로채 처리할 수 있는 구조적 확장성이 확보됩니다.
                    else -> onSearchIntent(intent)
                }
            }
        )
    }
}