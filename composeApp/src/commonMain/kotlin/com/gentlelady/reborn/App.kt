package com.gentlelady.reborn

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.gentlelady.reborn.feature.main.MainScreen // MainScreen 경로 확인
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.home.presentation.home.HomeIntent

@Composable
fun App(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit
) {
    MaterialTheme {
        // App은 전역 테마만 설정하고, 실제 뼈대는 MainScreen에 맡깁니다.
        MainScreen(
            homeState = state,
            onHomeIntent = onIntent
        )
    }
}