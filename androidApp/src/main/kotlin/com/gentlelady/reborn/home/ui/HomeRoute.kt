package com.gentlelady.reborn.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.gentlelady.reborn.App
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeViewModel
import com.gentlelady.reborn.search.presentation.SearchViewModel // 1. 검색 ViewModel 임포트
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute() {
    // 1. Koin을 통해 Shared 모듈의 두 뇌(ViewModel)를 모두 주입받음
    val homeViewModel: HomeViewModel = koinViewModel()
    val searchViewModel: SearchViewModel = koinViewModel() // 2. 검색 뇌 추가 주입

    // 2. 각 영역의 독립적인 MVI State 관찰(수집)
    val homeState by homeViewModel.state.collectAsState()
    val searchState by searchViewModel.state.collectAsState() // 3. 검색 상태 수집 추가

    // 3. 초기 데이터 로드 명령 (Intent)
    LaunchedEffect(Unit) {
        homeViewModel.handleIntent(HomeIntent.LoadFeed)
        // 만약 검색 화면 진입 시 초기 데이터 세팅이 필요하다면 여기에 의도를 흘려보낼 수 있습니다.
    }

    // 4. composeApp의 공통 진입점인 App() 호출 및 데이터 체인 완결
    App(
        homeState = homeState,
        onHomeIntent = { intent -> homeViewModel.handleIntent(intent) },
        searchState = searchState, // 4. 완성된 검색 상태 하향 주입
        onSearchIntent = { intent -> searchViewModel.dispatch(intent) } // 5. 완성된 검색 인텐트 핸들러 매핑
    )
}