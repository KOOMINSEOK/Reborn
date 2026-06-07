package com.gentlelady.reborn.feature.memorial_swipe

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.memorialNavGraph(
    navController: NavController
) {
    composable("memorial_swipe") {
        // 추후 Koin 등을 통해 실제 ViewModel과 State를 주입받을 위치입니다.
        val memorialState = MemorialSwipeState()

        MemorialSwipeScreen(
            state = memorialState,
            onIntent = { intent ->
                when (intent) {
                    // 💡 뒤로 가기 신호 가로채기: 메모리얼 화면을 닫고 이전 화면(Main)으로 돌아갑니다.
                    is MemorialSwipeIntent.NavigateHome -> {
                        navController.popBackStack()
                    }
                    // 팔로우, 공유 등 일반 액션은 해당 뷰모델로 넘깁니다.
                    else -> { /* memorialViewModel.onIntent(intent) */ }
                }
            }
        )
    }
}