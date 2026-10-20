package com.gentlelady.reborn.feature.feedwrite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import com.gentlelady.reborn.feedwrite.presentation.FeedPostType
import com.gentlelady.reborn.feedwrite.presentation.FeedWriteIntent
import com.gentlelady.reborn.feedwrite.presentation.FeedWriteState
import com.gentlelady.reborn.feedwrite.presentation.FeedWriteViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * "+" 버튼으로 진입하는 게시글 작성 플로우.
 * entry(바텀시트) → living(생전 게시글 작성) 또는 posthumous(사후 게시글 작성) → schedule(예약 설정) → target_select(대상 선택).
 * "feed_write" 그래프 하나로 묶어, 작성/예약 설정/대상 선택 화면이 FeedWriteViewModel을 공유하도록 한다.
 * 업로드가 끝나면 [onPosted](홈 피드 새로고침) 후 홈으로 돌아간다.
 */
fun NavGraphBuilder.feedWriteNavGraph(navController: NavController, onPosted: () -> Unit) {
    navigation(startDestination = "feed_write/entry", route = "feed_write") {
        dialog(
            route = "feed_write/entry",
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            FeedWriteEntryScreen(
                onDismiss = { navController.popBackStack("home", inclusive = false) },
                onNext = { type ->
                    val next = if (type == FeedPostType.LIVING) "feed_write/living" else "feed_write/posthumous"
                    navController.navigate(next) {
                        popUpTo("feed_write/entry") { inclusive = true }
                    }
                }
            )
        }

        composable("feed_write/living") { entry ->
            val viewModel = feedWriteViewModel(navController, entry)
            val state by viewModel.state.collectAsState()
            CloseWhenSubmitted(state, navController, onPosted)

            PostWriteScreen(
                title = "생전 게시글 작성",
                state = state,
                onIntent = viewModel::onIntent,
                onClose = { navController.popBackStack("home", inclusive = false) },
                onNext = { viewModel.onIntent(FeedWriteIntent.Submit(FeedPostType.LIVING)) }
            )
        }

        composable("feed_write/posthumous") { entry ->
            val viewModel = feedWriteViewModel(navController, entry)
            val state by viewModel.state.collectAsState()

            PostWriteScreen(
                title = "사후 게시글 작성",
                state = state,
                onIntent = viewModel::onIntent,
                onClose = { navController.popBackStack("home", inclusive = false) },
                onNext = { navController.navigate("feed_write/schedule") }
            )
        }

        composable("feed_write/schedule") { entry ->
            val viewModel = feedWriteViewModel(navController, entry)
            val state by viewModel.state.collectAsState()
            CloseWhenSubmitted(state, navController, onPosted)

            ScheduleSettingsScreen(
                state = state,
                onIntent = viewModel::onIntent,
                onBack = { navController.popBackStack() },
                onEditTargets = { navController.navigate("feed_write/target_select") },
                onComplete = { viewModel.onIntent(FeedWriteIntent.Submit(FeedPostType.POSTHUMOUS)) }
            )
        }

        composable("feed_write/target_select") { entry ->
            val viewModel = feedWriteViewModel(navController, entry)
            val state by viewModel.state.collectAsState()

            TargetSelectionScreen(
                state = state,
                onIntent = viewModel::onIntent,
                onBack = { navController.popBackStack() },
                onDone = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun feedWriteViewModel(navController: NavController, entry: NavBackStackEntry): FeedWriteViewModel {
    val parentEntry = remember(entry) { navController.getBackStackEntry("feed_write") }
    return koinViewModel(viewModelStoreOwner = parentEntry)
}

@Composable
private fun CloseWhenSubmitted(state: FeedWriteState, navController: NavController, onPosted: () -> Unit) {
    LaunchedEffect(state.isSubmitted) {
        if (state.isSubmitted) {
            onPosted()
            navController.popBackStack("home", inclusive = false)
        }
    }
}
