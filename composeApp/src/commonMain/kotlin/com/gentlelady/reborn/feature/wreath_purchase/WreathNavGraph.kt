// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/wreathpurchase/WreathNavGraph.kt
package com.gentlelady.reborn.feature.wreath_purchase

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gentlelady.reborn.wreathpurchase.presentation.WreathIntent
import com.gentlelady.reborn.wreathpurchase.presentation.WreathViewModel

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
                    else -> viewModel.onIntent(intent)
                }
            }
        )
    }
}
