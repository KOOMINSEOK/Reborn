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
import com.gentlelady.reborn.profile.presentation.ProfileState
import com.gentlelady.reborn.profile.presentation.ProfileIntent

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
    profileState: ProfileState,
    onProfileIntent: (ProfileIntent) -> Unit
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
            profileState = profileState,
            onProfileIntent = { intent ->
                when (intent) {
                    // 💡 [수정 완료] 프로필 토글 버튼 터치 시 "memorial/me" (내 메모리얼 스크린)로 이동!
                    is ProfileIntent.ClickToggleMemorialMode -> {
                        navController.navigate("memorial/me")
                    }
                    else -> onProfileIntent(intent)
                }
            }
        )
    }
}