package com.gentlelady.reborn.feature.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.search.presentation.SearchState
import com.gentlelady.reborn.search.presentation.SearchIntent
import com.gentlelady.reborn.message.presentation.MessageState
import com.gentlelady.reborn.message.presentation.MessageIntent
import com.gentlelady.reborn.profile.presentation.ProfileState   // 🆕 임포트
import com.gentlelady.reborn.profile.presentation.ProfileIntent  // 🆕 임포트

/**
 * App.kt에서 목차로 호출하는 메인 전체 플로우 네비게이션 그래프
 */
fun NavGraphBuilder.mainNavGraph(
    navController: NavController,
    homeState: HomeState,
    onHomeIntent: (HomeIntent) -> Unit,
    searchState: SearchState,
    onSearchIntent: (SearchIntent) -> Unit,
    messageState: MessageState,
    onMessageIntent: (MessageIntent) -> Unit,
    profileState: ProfileState,              // 🆕 프로필 상태 주입 받음
    onProfileIntent: (ProfileIntent) -> Unit // 🆕 프로필 인텐트 핸들러 주입 받음
) {
    composable("main_flow") {
        MainScreen(
            homeState = homeState,
            onHomeIntent = { intent ->
                when (intent) {
                    is HomeIntent.ClickMemorialIcon -> {
                        navController.navigate("memorial_swipe")
                    }
                    else -> onHomeIntent(intent)
                }
            },
            searchState = searchState,
            onSearchIntent = onSearchIntent,
            messageState = messageState,
            onMessageIntent = onMessageIntent,
            profileState = profileState,       // 🆕 MainScreen으로 전달
            onProfileIntent = { intent ->      // 🆕 프로필 인텐트 라우팅 제어부 개설
                when (intent) {
                    // 💡 규칙 수호: 프로필 우상단 토글 버튼 터치 시, 탭바가 없는 완전 몰입형 메모리얼 화면으로 즉시 이송
                    is ProfileIntent.ClickToggleMemorialMode -> {
                        navController.navigate("memorial_swipe")
                    }
                    else -> onProfileIntent(intent)
                }
            }
        )
    }
}