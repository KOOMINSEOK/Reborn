// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/wreathpurchase/WreathNavGraph.kt
package com.gentlelady.reborn.feature.wreath_purchase

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gentlelady.reborn.memorial.presentation.MemorialIntent
import com.gentlelady.reborn.memorial.presentation.MemorialViewModel
import com.gentlelady.reborn.memorial.presentation.MemorialWreathItem
import com.gentlelady.reborn.wreathpurchase.presentation.WreathIntent
import com.gentlelady.reborn.wreathpurchase.presentation.WreathViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random

fun NavGraphBuilder.wreathNavGraph(navController: NavHostController) {
    composable("wreath/purchase") {
        val viewModel: WreathViewModel = viewModel { WreathViewModel() }
        val state by viewModel.state.collectAsState()

        WreathPurchaseScreen(
            selectedTier = state.selectedTier,
            onIntent = { intent ->
                when (intent) {
                    is WreathIntent.ClickBack -> navController.popBackStack()
                    is WreathIntent.ClickBuy -> navController.navigate("wreath/checkout/${intent.tier}")
                    else -> viewModel.onIntent(intent)
                }
            }
        )
    }

    composable(
        route = "wreath/checkout/{tier}",
        arguments = listOf(navArgument("tier") { type = NavType.StringType })
    ) { backStackEntry ->
        val viewModel: WreathViewModel = viewModel { WreathViewModel() }
        val state by viewModel.state.collectAsState()
        val tier = backStackEntry.arguments?.getString("tier")

        WreathCheckoutScreen(
            tier = tier,
            selectedPaymentMethod = state.selectedPaymentMethod,
            onIntent = { intent ->
                when (intent) {
                    is WreathIntent.ClickBack -> navController.popBackStack()
                    is WreathIntent.ClickPay -> navController.navigate("wreath/message/${intent.tier}")
                    else -> viewModel.onIntent(intent)
                }
            }
        )
    }

    composable(
        route = "wreath/message/{tier}",
        arguments = listOf(navArgument("tier") { type = NavType.StringType })
    ) { backStackEntry ->
        val viewModel: WreathViewModel = viewModel { WreathViewModel() }
        val state by viewModel.state.collectAsState()
        val tier = backStackEntry.arguments?.getString("tier")

        // 💡 "memorial/me" 백스택 엔트리와 ViewModel을 공유해서, 화환 남기기 결과를 메모리얼 화환 탭에 바로 반영한다.
        val memorialEntry = remember(backStackEntry) { navController.getBackStackEntry("memorial/me") }
        val memorialViewModel: MemorialViewModel = koinViewModel(viewModelStoreOwner = memorialEntry)

        val onIntent: (WreathIntent) -> Unit = { intent ->
            when (intent) {
                is WreathIntent.ClickBack -> navController.popBackStack()
                is WreathIntent.ClickSubmitMessage -> {
                    memorialViewModel.onIntent(
                        MemorialIntent.AddOnlineWreathItem(
                            MemorialWreathItem(
                                id = "wreath_${Random.nextLong()}",
                                organizationName = state.senderName.ifBlank { "익명" }
                            )
                        )
                    )
                    navController.popBackStack("memorial/me", inclusive = false)
                }
                else -> viewModel.onIntent(intent)
            }
        }

        if (tier == "special") {
            WreathSpecialMessageScreen(
                tier = tier,
                selectedFlowerDesign = state.selectedFlowerDesign,
                messageText = state.messageText,
                senderName = state.senderName,
                onIntent = onIntent
            )
        } else {
            WreathMessageScreen(
                tier = tier,
                messageText = state.messageText,
                senderName = state.senderName,
                onIntent = onIntent
            )
        }
    }
}
