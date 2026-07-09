package com.gentlelady.reborn.feature.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.search.presentation.SearchState
import com.gentlelady.reborn.search.presentation.SearchIntent
import com.gentlelady.reborn.message.presentation.MessageState  // 메시지 상태 임포트
import com.gentlelady.reborn.message.presentation.MessageIntent // 메시지 인텐트 임포트

/**
 * App.kt에서 목차로 호출하는 메인 전체 플로우 네비게이션 그래프
 */
fun NavGraphBuilder.mainNavGraph(
    navController: NavController,
    homeState: HomeState,
    onHomeIntent: (HomeIntent) -> Unit,
    searchState: SearchState,
    onSearchIntent: (SearchIntent) -> Unit,
    messageState: MessageState,              // 외부로부터 메시지 상태 주입 받음
    onMessageIntent: (MessageIntent) -> Unit // 외부로부터 메시지 인텐트 핸들러 주입 받음
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
            searchState = searchState,
            onSearchIntent = onSearchIntent,
            messageState = messageState,            // MainScreen으로 메시지 상태 전달
            onMessageIntent = { intent ->
                when (intent) {
                    // 💡 [확장성] 추후 채팅방 클릭(ClickChatRoom) 시 바텀바가 없는 몰입형 채팅방 상세로
                    // 가야 한다면 여기서 'navController.navigate("chat_detail_flow")' 등으로 가로챕니다.
                    else -> onMessageIntent(intent)
                }
            }
        )
    }
}