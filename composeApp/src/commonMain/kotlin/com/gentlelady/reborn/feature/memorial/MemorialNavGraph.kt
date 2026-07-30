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
                        if (state.isEditingProfile || state.selectedHistoryIndex != null || state.isWritingHistory) {
                            // 💡 편집 중이거나 히스토리 상세를 보는 중이면 그 화면만 닫고 메모리얼 메인으로 복귀
                            viewModel.onIntent(intent)
                        } else {
                            // 💡 메모리얼 메인 화면에서 뒤로가기를 누르면 MyProfile 화면으로 복귀
                            navController.popBackStack()
                        }
                    }
                    else -> {
                        viewModel.onIntent(intent)
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
                        if (state.isEditingProfile || state.selectedHistoryIndex != null || state.isWritingHistory) {
                            viewModel.onIntent(intent)
                        } else {
                            navController.popBackStack()
                        }
                    }
                    else -> {
                        viewModel.onIntent(intent)
                    }
                }
            }
        )
    }
}