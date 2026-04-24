package com.gentlelady.reborn

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.gentlelady.reborn.home.ui.HomeScreen
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.home.presentation.home.HomeIntent

@Composable
fun App(
    // 나중에는 여기에 내비게이션 객체가 들어오겠지만,
    // 지금은 홈 화면의 데이터와 인텐트를 직접 전달받도록 설계합니다.
    state: HomeState,
    onIntent: (HomeIntent) -> Unit
) {
    MaterialTheme {
        // App은 이제 전역적인 테마, 스캐폴드(Scaffold),
        // 그리고 내비게이션 호스트 역할을 수행하게 됩니다.
        HomeScreen(
            state = state,
            onIntent = onIntent
        )
    }
}