package com.gentlelady.reborn.feature.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gentlelady.reborn.feature.home.HomeScreen
import com.gentlelady.reborn.feature.search.searchNavGraph
import com.gentlelady.reborn.feature.message.MessageScreen // 메시지 메인 스크린 임포트
import com.gentlelady.reborn.feature.profile.ProfileScreen
import com.gentlelady.reborn.feature.profile.profileNavGraph
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.search.presentation.SearchState
import com.gentlelady.reborn.search.presentation.SearchIntent
import com.gentlelady.reborn.message.presentation.MessageState
import com.gentlelady.reborn.message.presentation.MessageIntent
import com.gentlelady.reborn.profile.presentation.ProfileIntent
import com.gentlelady.reborn.profile.presentation.ProfileState

@Composable
fun MainScreen(
    homeState: HomeState,
    onHomeIntent: (HomeIntent) -> Unit,
    searchState: SearchState,
    onSearchIntent: (SearchIntent) -> Unit,
    messageState: MessageState,             // 메시지 상태 추가 주입
    onMessageIntent: (MessageIntent) -> Unit, // 메시지 인텐트 핸들러 추가 주입
    profileState: ProfileState,             // 🆕 프로필 상태 추가 주입
    onProfileIntent: (ProfileIntent) -> Unit // 🆕 프로필 인텐트 핸들러 추가 주입
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            val mainRoutes = listOf("home", "search", "message", "profile")
            if (currentRoute in mainRoutes) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("home") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. 홈 화면 슬롯
            composable("home") {
                HomeScreen(
                    state = homeState,
                    onIntent = onHomeIntent
                )
            }

            // 2. 검색 화면 그래프 조립 (Table of Contents 확장 함수 구조)
            searchNavGraph(
                state = searchState,
                onIntent = onSearchIntent
            )

            // 3. 메시지 화면 슬롯 연결 완료 (MockData 수집 상태 연동 완료)
            composable("message") {
                MessageScreen(
                    state = messageState,
                    onIntent = onMessageIntent
                )
            }

            // 4. 프로필 화면 슬롯 (추후 연동 준비)
            profileNavGraph(
                state = profileState,
                onIntent = onProfileIntent // 상위 App.kt로 인텐트 전달
            )
        }
    }
}