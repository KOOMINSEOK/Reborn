package com.gentlelady.reborn

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gentlelady.reborn.feature.main.mainNavGraph
import com.gentlelady.reborn.feature.memorial_swipe.memorialNavGraph
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.search.presentation.SearchState
import com.gentlelady.reborn.search.presentation.SearchIntent
import com.gentlelady.reborn.message.presentation.MessageState   // 1. 메시지 상태 임포트 추가
import com.gentlelady.reborn.message.presentation.MessageIntent  // 2. 메시지 인텐트 임포트 추가

@Composable
fun App(
    homeState: HomeState,
    onHomeIntent: (HomeIntent) -> Unit,
    searchState: SearchState,
    onSearchIntent: (SearchIntent) -> Unit,
    messageState: MessageState,               // 3. 메시지 상태 추가 주입
    onMessageIntent: (MessageIntent) -> Unit  // 4. 메시지 인텐트 핸들러 추가 주입
) {
    MaterialTheme {
        // 앱 전체 화면을 덮고 전환하는 최상위 라우터
        val rootNavController = rememberNavController()

        NavHost(
            navController = rootNavController,
            startDestination = "main_flow" // 앱을 켜면 탭바가 있는 메인 플로우부터 시작
        ) {
            // 1. 메인 기능 그래프 조립 (하단 탭바 내부에서 Home, Search, Message 목차 분산 처리)
            mainNavGraph(
                navController = rootNavController,
                homeState = homeState,
                onHomeIntent = onHomeIntent,
                searchState = searchState,
                onSearchIntent = onSearchIntent,
                messageState = messageState,       // 5. 메시지 상태를 메인 그래프로 전달
                onMessageIntent = onMessageIntent  // 6. 메시지 인텐트 파이프라인 전달
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