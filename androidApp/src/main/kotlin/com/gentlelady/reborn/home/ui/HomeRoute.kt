package com.gentlelady.reborn.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.gentlelady.reborn.App  // 1. App import 추가
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute() {
    // 1. Koin을 통해 Shared 모듈의 ViewModel 주입
    val viewModel: HomeViewModel = koinViewModel()

    // 2. State 수집
    val state by viewModel.state.collectAsState()

    // 3. 초기 데이터 로드 명령 (Intent)
    LaunchedEffect(Unit) {
        viewModel.handleIntent(HomeIntent.LoadFeed)
    }

    // 4. composeApp의 공통 진입점인 App()을 호출
    // 여기서 state와 onIntent를 넘겨주면, App.kt가 이를 받아 HomeScreen에 전달합니다.
    App(
        state = state,
        onIntent = { intent -> viewModel.handleIntent(intent) }
    )
}