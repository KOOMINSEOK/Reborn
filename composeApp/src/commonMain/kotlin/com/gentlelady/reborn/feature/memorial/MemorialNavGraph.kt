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
import com.gentlelady.reborn.core.designsystem.comments.CommentCountEffect
import com.gentlelady.reborn.core.network.InteractionTarget

fun NavGraphBuilder.memorialNavGraph(
    navController: NavHostController,
    onWritingHistoryChange: (Boolean) -> Unit = {}
) {
    // "memorial/me" 는 legacy 라우트명 — 실제로는 데모 추모 페이지를 연다 (편집 개념 없음).
    composable("memorial/me") {
        MemorialRoute(navController, memorialId = DEMO_MEMORIAL_ID, onWritingHistoryChange = onWritingHistoryChange)
    }
    // focusGuestbook = 내 방명록에서 눌러 들어올 때, 방명록 탭을 그 글 위치로 연다
    composable(
        route = "memorial/other/{memorialId}?focusGuestbook={focusGuestbook}",
        arguments = listOf(
            navArgument("memorialId") { type = NavType.StringType },
            navArgument("focusGuestbook") { type = NavType.StringType; nullable = true; defaultValue = null },
        ),
    ) { backStackEntry ->
        val id = backStackEntry.arguments?.getString("memorialId") ?: DEMO_MEMORIAL_ID
        val focus = backStackEntry.arguments?.getString("focusGuestbook")
        MemorialRoute(navController, memorialId = id, focusGuestBookId = focus, onWritingHistoryChange = onWritingHistoryChange)
    }
}

@androidx.compose.runtime.Composable
private fun MemorialRoute(
    navController: NavHostController,
    memorialId: String,
    onWritingHistoryChange: (Boolean) -> Unit,
    focusGuestBookId: String? = null,
) {
    val viewModel: MemorialViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(memorialId, focusGuestBookId) {
        viewModel.onIntent(MemorialIntent.LoadMemorial(memorialId, focusGuestBookId))
    }
    LaunchedEffect(state.isWritingHistory) {
        onWritingHistoryChange(state.isWritingHistory)
    }
    // 추억 글 댓글 수 (시트를 닫은 뒤 끝난 작성·삭제도)
    CommentCountEffect { change ->
        if (change.target == InteractionTarget.HISTORY) {
            viewModel.onIntent(MemorialIntent.HistoryCommentCountChanged(change.parentId, change.delta))
        }
    }
    DisposableEffect(Unit) {
        onDispose { onWritingHistoryChange(false) }
    }

    MemorialScreen(
        state = state,
        onUserClick = { navController.navigate("user/$it") },
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
