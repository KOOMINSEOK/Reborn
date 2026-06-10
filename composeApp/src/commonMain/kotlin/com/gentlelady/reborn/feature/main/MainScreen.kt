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
import com.gentlelady.reborn.feature.search.searchNavGraph // 1. 검색 네비게이션 확장 함수 임포트
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.search.presentation.SearchState
import com.gentlelady.reborn.search.presentation.SearchIntent

@Composable
fun MainScreen(
    homeState: HomeState,
    onHomeIntent: (HomeIntent) -> Unit,
    searchState: SearchState,             // 2. 검색 상태 추가 주입
    onSearchIntent: (SearchIntent) -> Unit // 3. 검색 인텐트 핸들러 추가 주입
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
            composable("home") {
                HomeScreen(
                    state = homeState,
                    onIntent = onHomeIntent
                )
            }

            // 4. 확장 함수(Table of Contents) 구조로 SearchScreen 깨끗하게 연결
            searchNavGraph(
                state = searchState,
                onIntent = onSearchIntent
            )

            composable("message") { /* MessageScreen() */ }
            composable("profile") { /* ProfileScreen() */ }
        }
    }
}