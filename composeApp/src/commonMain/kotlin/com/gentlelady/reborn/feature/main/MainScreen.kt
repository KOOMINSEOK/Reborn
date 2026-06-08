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
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.home.presentation.home.HomeIntent

@Composable
fun MainScreen(
    homeState: HomeState,             // 추가
    onHomeIntent: (HomeIntent) -> Unit // 추가
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
                            // "home" 문자열을 직접 사용하여 Unresolved reference 'id' 에러 방지
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
            // ✅ 요청하신 home 경로 수정 부분
            composable("home") {
                HomeScreen(
                    state = homeState,
                    onIntent = onHomeIntent
                )
            }

            composable("search") { /* SearchScreen() */ }
            composable("message") { /* MessageScreen() */ }
            composable("profile") { /* ProfileScreen() */ }
        }
    }
}