package com.gentlelady.reborn.feature.onboarding

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.onboarding.presentation.OnboardingIntent
import com.gentlelady.reborn.onboarding.presentation.OnboardingState

fun NavGraphBuilder.onboardingNavGraph(
    navController: NavController,
    onboardingState: OnboardingState,
    onOnboardingIntent: (OnboardingIntent) -> Unit
) {
    composable("onboarding") {
        // 온보딩이 끝나면 메인 플로우로 이동 (로그인 화면이 생기면 이 목적지를 교체)
        LaunchedEffect(onboardingState.isCompleted) {
            if (onboardingState.isCompleted) {
                navController.navigate("main_flow") {
                    popUpTo("onboarding") { inclusive = true }
                }
            }
        }

        OnboardingScreen(
            state = onboardingState,
            onIntent = onOnboardingIntent
        )
    }
}
