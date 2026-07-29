// com/gentlelady/reborn/feature/main/MainNavGraph.kt
package com.gentlelady.reborn.feature.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.message.presentation.MessageIntent
import com.gentlelady.reborn.message.presentation.MessageState
import com.gentlelady.reborn.profile.presentation.ProfileIntent
import com.gentlelady.reborn.profile.presentation.ProfileState
import com.gentlelady.reborn.search.presentation.SearchIntent
import com.gentlelady.reborn.search.presentation.SearchState

fun NavGraphBuilder.mainNavGraph(
    navController: NavController,
    homeState: HomeState,
    onHomeIntent: (HomeIntent) -> Unit,
    searchState: SearchState,
    onSearchIntent: (SearchIntent) -> Unit,
    messageState: MessageState,
    onMessageIntent: (MessageIntent) -> Unit,
    profileState: ProfileState,
    onProfileIntent: (ProfileIntent) -> Unit
) {
    composable("main_flow") {
        MainScreen(
            homeState = homeState,
            onHomeIntent = { intent ->
                when (intent) {
                    is HomeIntent.ClickMemorialIcon -> {
                        navController.navigate("memorial_swipe")
                    }
                    else -> onHomeIntent(intent)
                }
            },
            searchState = searchState,
            onSearchIntent = onSearchIntent,
            messageState = messageState,
            onMessageIntent = onMessageIntent,
            profileState = profileState,
            onProfileIntent = onProfileIntent // 💡 MainScreen 내부 핸들러로 위임
        )
    }
}