package com.gentlelady.reborn.feature.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeState

// 💡 NavGraphBuilder의 확장 함수로 만들어 App.kt에서 깔끔하게 호출할 수 있게 합니다.
// composeApp 내부의 MainNavGraph.kt
fun NavGraphBuilder.mainNavGraph(
    navController: NavController,
    homeState: HomeState,
    onHomeIntent: (HomeIntent) -> Unit
) {
    composable("main_flow") {
        MainScreen(
            homeState = homeState,
            onHomeIntent = { intent ->
                when (intent) {
                    // 💡 여기서 가로챕니다! (스크린 전환 로직)
                    is HomeIntent.ClickMemorialIcon -> {
                        navController.navigate("memorial_swipe")
                    }
                    // LoadFeed 같은 다른 로직은 shared에 있는 ViewModel로 흘려보냄
                    else -> onHomeIntent(intent)
                }
            }
        )
    }
}