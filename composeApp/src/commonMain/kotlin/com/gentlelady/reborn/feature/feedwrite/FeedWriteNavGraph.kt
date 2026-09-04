package com.gentlelady.reborn.feature.feedwrite

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog

/**
 * "+" 버튼으로 진입하는 게시글 작성 플로우.
 * entry(바텀시트) → living(생전 게시글 작성) 두 화면만 존재한다.
 * 사후 게시글 화면은 아직 없어 선택해도 다음 화면으로 넘어가지 않는다.
 */
fun NavGraphBuilder.feedWriteNavGraph(navController: NavController) {
    dialog(
        route = "feed_write/entry",
        dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        FeedWriteEntryScreen(
            onDismiss = { navController.popBackStack() },
            onNext = { type ->
                if (type == FeedPostType.LIVING) {
                    navController.navigate("feed_write/living") {
                        popUpTo("feed_write/entry") { inclusive = true }
                    }
                } else {
                    navController.popBackStack()
                }
            }
        )
    }

    composable("feed_write/living") {
        LivingPostWriteScreen(
            onClose = { navController.popBackStack("home", inclusive = false) },
            onSubmit = { navController.popBackStack("home", inclusive = false) }
        )
    }
}
