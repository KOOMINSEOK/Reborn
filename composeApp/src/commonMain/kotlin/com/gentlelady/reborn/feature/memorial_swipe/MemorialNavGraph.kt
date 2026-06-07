// 📄 composeApp/.../feature/memorial_swipe/MemorialNavGraph.kt
package com.gentlelady.reborn.feature.memorial_swipe

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.data.MockDataSource // 💡 MockDataSource 가져오기
import com.gentlelady.reborn.memorial_swipe.presentation.MemorialSwipeState
import com.gentlelady.reborn.memorial_swipe.presentation.MemorialSwipeIntent

fun NavGraphBuilder.memorialNavGraph(
    navController: NavController
) {
    composable("memorial_swipe") {
        // 💡 1. 텅 빈 State 대신, MockDataSource의 데이터를 사용하여 초기화합니다.
        val memorialState = MemorialSwipeState(
            memorialItems = MockDataSource.memorialItems
        )

        MemorialSwipeScreen(
            state = memorialState,
            onIntent = { intent ->
                when (intent) {
                    is MemorialSwipeIntent.NavigateHome -> {
                        navController.popBackStack()
                    }
                    else -> { /* 나중에 ViewModel 연결 */ }
                }
            }
        )
    }
}