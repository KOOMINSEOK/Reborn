// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/memorial/MemorialNavGraph.kt
package com.gentlelady.reborn.feature.memorial

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.gentlelady.reborn.memorial.presentation.MemorialIntent
import com.gentlelady.reborn.memorial.presentation.MemorialViewModel

fun NavGraphBuilder.memorialNavGraph(
    navController: NavHostController
) {
    // 1. 내 메모리얼 공간 라우트 ("memorial/me")
    composable("memorial/me") {
        val viewModel: MemorialViewModel = viewModel { MemorialViewModel() }
        val state by viewModel.state.collectAsState()

        MemorialScreen(
            state = state,
            onIntent = { intent ->
                when (intent) {
                    is MemorialIntent.ClickBack -> {
                        navController.popBackStack() // 뒤로가기 클릭 시 프로필로 복귀
                    }
                    else -> {
                        viewModel.onIntent(intent) // 💡 viewModel에 Intent 전달
                    }
                }
            }
        )
    }

    // 2. 타인 메모리얼 공간 라우트 ("memorial/other/{memorialId}")
    composable("memorial/other/{memorialId}") {
        val viewModel: MemorialViewModel = viewModel { MemorialViewModel() }
        val state by viewModel.state.collectAsState()

        MemorialScreen(
            state = state,
            onIntent = { intent ->
                when (intent) {
                    is MemorialIntent.ClickBack -> {
                        navController.popBackStack()
                    }
                    else -> {
                        viewModel.onIntent(intent)
                    }
                }
            }
        )
    }
}