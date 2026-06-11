package com.gentlelady.reborn

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gentlelady.reborn.feature.main.mainNavGraph
import com.gentlelady.reborn.feature.memorial_swipe.memorialNavGraph
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.search.presentation.SearchState  // 1. 검색 상태 임포트
import com.gentlelady.reborn.search.presentation.SearchIntent // 2. 검색 인텐트 임포트

@Composable
fun App(
    homeState: HomeState,                  // 3. 파라미터명 명확화 (state -> homeState)
    onHomeIntent: (HomeIntent) -> Unit,    // 4. 파라미터명 명확화 (onIntent -> onHomeIntent)
    searchState: SearchState,              // 5. 검색 상태 추가 주입
    onSearchIntent: (SearchIntent) -> Unit  // 6. 검색 인텐트 추가 주입
) {
    MaterialTheme {
        // 앱 전체 화면을 덮고 전환하는 최상위 라우터
        val rootNavController = rememberNavController()

        NavHost(
            navController = rootNavController,
            startDestination = "main_flow" // 앱을 켜면 탭바가 있는 메인 플로우부터 시작
        ) {
            // 1. 메인 기능 그래프 조립 (하단 탭바 내부에서 Home과 Search 목차 분산 처리)
            mainNavGraph(
                navController = rootNavController,
                homeState = homeState,
                onHomeIntent = onHomeIntent,
                searchState = searchState,       // 7. 교정된 검색 상태 전달
                onSearchIntent = onSearchIntent  // 8. 교정된 검색 인텐트 핸들러 전달
            )

            // 2. 메모리얼 기능 그래프 조립 (탭바가 없는 완전 몰입형 화면)
            memorialNavGraph(
                navController = rootNavController
            )

            // 3. (추후 추가) 설정 화면, 로그인 화면 등...
            // settingNavGraph(navController = rootNavController)
        }
    }
}