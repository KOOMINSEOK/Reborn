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
import com.gentlelady.reborn.message.presentation.MessageState
import com.gentlelady.reborn.message.presentation.MessageIntent
import com.gentlelady.reborn.profile.presentation.ProfileState   // 🆕 프로필 상태 임포트 추가
import com.gentlelady.reborn.profile.presentation.ProfileIntent  // 🆕 프로필 인텐트 임포트 추가

@Composable
fun App(
    homeState: HomeState,
    onHomeIntent: (HomeIntent) -> Unit,
    searchState: SearchState,
    onSearchIntent: (SearchIntent) -> Unit,
    messageState: MessageState,
    onMessageIntent: (MessageIntent) -> Unit,
    profileState: ProfileState,               // 🆕 프로필 상태 주입 추가
    onProfileIntent: (ProfileIntent) -> Unit  // 🆕 프로필 인텐트 핸들러 주입 추가
) {
    MaterialTheme {
        val rootNavController = rememberNavController()

        NavHost(
            navController = rootNavController,
            startDestination = "main_flow"
        ) {
            // 1. 메인 기능 그래프 조립 (프로필 파이프라인 결합 완료)
            mainNavGraph(
                navController = rootNavController,
                homeState = homeState,
                onHomeIntent = onHomeIntent,
                searchState = searchState,
                onSearchIntent = onSearchIntent,
                messageState = messageState,
                onMessageIntent = onMessageIntent,
                profileState = profileState,       // 🆕 전달
                onProfileIntent = onProfileIntent  // 🆕 전달
            )

            // 2. 메모리얼 기능 그래프 조립 (탭바가 없는 완전 몰입형 화면)
            memorialNavGraph(
                navController = rootNavController
            )
        }
    }
}