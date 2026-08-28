// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/memorial/MemorialNavGraph.kt
package com.gentlelady.reborn.feature.memorial

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gentlelady.reborn.memorial.presentation.DEMO_MEMORIAL_ID
import com.gentlelady.reborn.memorial.presentation.MemorialIntent
import com.gentlelady.reborn.memorial.presentation.MemorialViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.memorialNavGraph(
    navController: NavHostController,
    onWritingHistoryChange: (Boolean) -> Unit = {}
) {
    // "memorial/me" 는 legacy 라우트명 — 실제로는 데모 추모 페이지를 연다 (편집 개념 없음).
    composable("memorial/me") {
        MemorialRoute(navController, memorialId = DEMO_MEMORIAL_ID, onWritingHistoryChange = onWritingHistoryChange)
    }
    composable(
        route = "memorial/other/{memorialId}",
        arguments = listOf(navArgument("memorialId") { type = NavType.StringType }),
    ) { backStackEntry ->
        val id = backStackEntry.arguments?.getString("memorialId") ?: DEMO_MEMORIAL_ID
        MemorialRoute(navController, memorialId = id, onWritingHistoryChange = onWritingHistoryChange)
    }
}

@androidx.compose.runtime.Composable
private fun MemorialRoute(
    navController: NavHostController,
    memorialId: String,
    onWritingHistoryChange: (Boolean) -> Unit,
) {
    val viewModel: MemorialViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(memorialId) {
        viewModel.onIntent(MemorialIntent.LoadMemorial(memorialId))
    }
    LaunchedEffect(state.isWritingHistory) {
        onWritingHistoryChange(state.isWritingHistory)
    }
    DisposableEffect(Unit) {
        onDispose { onWritingHistoryChange(false) }
    }

    MemorialScreen(
        state = state,
        onIntent = { intent ->
            when (intent) {
                is MemorialIntent.ClickBack -> {
                    if (state.selectedHistoryIndex != null || state.isWritingHistory) {
                        viewModel.onIntent(intent)
                    } else {
                        navController.popBackStack()
                    }
                }
                is MemorialIntent.ClickPurchaseWreath -> navController.navigate("wreath/purchase")
                else -> viewModel.onIntent(intent)
            }
        },
    )
}
