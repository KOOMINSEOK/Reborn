package com.gentlelady.reborn.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.gentlelady.reborn.App
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeViewModel
import com.gentlelady.reborn.search.presentation.SearchViewModel
import com.gentlelady.reborn.message.presentation.MessageViewModel // 1. 메시지 ViewModel 임포트 추가
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute() {
    // 1. Koin을 통해 Shared 모듈의 세 개의 뇌(ViewModel)를 모두 주입받음
    val homeViewModel: HomeViewModel = koinViewModel()
    val searchViewModel: SearchViewModel = koinViewModel()
    val messageViewModel: MessageViewModel = koinViewModel() // 2. 메시지 뇌 추가 주입

    // 2. 각 영역의 독립적인 MVI State 관찰(수집) 및 단일 진실 공급원(SSOT) 수립
    val homeState by homeViewModel.state.collectAsState()
    val searchState by searchViewModel.state.collectAsState()
    val messageState by messageViewModel.state.collectAsState() // 3. 메시지 상태 수집 추가

    // 3. 초기 데이터 로드 명령 (Intent) - 최초 화면 진입 시 트리거용
    LaunchedEffect(Unit) {
        homeViewModel.handleIntent(HomeIntent.LoadFeed)
        // 추후 메시지 목록이나 방명록 피드를 서버/로컬 DB에서 당겨와야 할 때 활성화
        // messageViewModel.handleIntent(MessageIntent.LoadMessageTabs)
    }

    // 4. composeApp의 공통 진입점인 App() 호출 및 완벽한 최상위 데이터 파이프라인 완결
    App(
        homeState = homeState,
        onHomeIntent = { intent -> homeViewModel.handleIntent(intent) },
        searchState = searchState,
        onSearchIntent = { intent -> searchViewModel.dispatch(intent) },
        messageState = messageState, // 4. 수집된 메시지 상태 파라미터 하향 주입
        onMessageIntent = { intent -> messageViewModel.handleIntent(intent) } // 5. 메시지 인텐트 핸들러 바인딩 완료
    )
}