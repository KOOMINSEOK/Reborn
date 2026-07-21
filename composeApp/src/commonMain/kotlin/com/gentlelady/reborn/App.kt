package com.gentlelady.reborn

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gentlelady.reborn.feature.main.mainNavGraph
import com.gentlelady.reborn.feature.memorial.memorialNavGraph
import com.gentlelady.reborn.feature.memorial_swipe.memorialSwipeNavGraph
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.message.presentation.MessageIntent
import com.gentlelady.reborn.message.presentation.MessageState
import com.gentlelady.reborn.profile.presentation.ProfileIntent
import com.gentlelady.reborn.profile.presentation.ProfileState
import com.gentlelady.reborn.search.presentation.SearchIntent
import com.gentlelady.reborn.search.presentation.SearchState

@Composable
fun App(
    homeState: HomeState,
    onHomeIntent: (HomeIntent) -> Unit,
    searchState: SearchState,
    onSearchIntent: (SearchIntent) -> Unit,
    messageState: MessageState,
    onMessageIntent: (MessageIntent) -> Unit,
    profileState: ProfileState,
    onProfileIntent: (ProfileIntent) -> Unit
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
                profileState = profileState,
                onProfileIntent = onProfileIntent
            )

            // 2. 메모리얼 스와이프 전용 기능 그래프 (기존 기능)
            memorialSwipeNavGraph(
                navController = rootNavController
            )

            // 3. 🆕 추모/기념 상세 페이지 그래프 (내 공간 / 타인 공간 상세)
            memorialNavGraph(
                navController = rootNavController
            )
        }
    }
}