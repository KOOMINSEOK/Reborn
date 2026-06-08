package com.gentlelady.reborn

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gentlelady.reborn.feature.main.mainNavGraph
import com.gentlelady.reborn.feature.memorial_swipe.memorialNavGraph
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.home.presentation.home.HomeIntent

@Composable
fun App(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit
) {
    MaterialTheme {
        // 앱 전체 화면을 덮고 전환하는 최상위 라우터
        val rootNavController = rememberNavController()

        NavHost(
            navController = rootNavController,
            startDestination = "main_flow" // 앱을 켜면 탭바가 있는 메인 플로우부터 시작
        ) {
            // 1. 메인 기능 그래프 조립 (탭바 O)
            mainNavGraph(
                navController = rootNavController,
                homeState = state,
                onHomeIntent = onIntent
            )

            // 2. 메모리얼 기능 그래프 조립 (탭바 X)
            memorialNavGraph(
                navController = rootNavController
            )

            // 3. (추후 추가) 설정 화면, 로그인 화면 등...
            // settingNavGraph(navController = rootNavController)
        }
    }
}