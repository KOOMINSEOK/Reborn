package com.gentlelady.reborn.feature.message

import androidx.compose.runtime.*
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.data.MockDataSource
import com.gentlelady.reborn.message.presentation.MessageIntent
import com.gentlelady.reborn.message.presentation.MessageState
import com.gentlelady.reborn.message.presentation.MessageTab

/**
 * Reborn 네비게이션 규칙: App.kt의 목차 역할을 수행하기 위한 확장 함수 구조
 */
fun NavGraphBuilder.messageNavGraph(
    onNavigateToChatDetail: (String) -> Unit,
    onNavigateToGuestBookDetail: (String) -> Unit
) {
    composable(route = "message_main") {

        // 1. 프로토타입 구동을 위해 MockDataSource를 기본값으로 품은 가변 상태(State) 선언
        var uiState by remember {
            mutableStateOf(
                MessageState(
                    currentTab = MessageTab.MESSAGE,
                    chatRooms = MockDataSource.messageChatRooms,   // 가공해 둔 덤프 데이터 연결
                    guestBooks = MockDataSource.messageGuestBooks  // 가공해 둔 덤프 데이터 연결
                )
            )
        }

        // 2. 단방향 MVI Intent 가로채기 파이프라인 (실제 앱 구동 시 작동 활성화용)
        val onIntent: (MessageIntent) -> Unit = { intent ->
            when (intent) {
                is MessageIntent.SelectTab -> {
                    // 유저가 탭을 누르면 State를 변경하여 화면 리렌더링 및 하단 파란줄 이동 트리거
                    uiState = uiState.copy(currentTab = intent.tab)
                }
                is MessageIntent.UpdateSearchQuery -> {
                    // 검색창에 글자를 치면 키보드 입력 값이 실시간 반영되도록 동적 매핑
                    uiState = uiState.copy(searchQuery = intent.query)
                }
                is MessageIntent.ClickChatRoom -> {
                    // 규칙 준수: 네비게이션 제어 로직은 하위 Screen이 아닌 NavHost 컨텍스트 람다로 밖으로 던짐
                    onNavigateToChatDetail(intent.roomId)
                }
                is MessageIntent.ClickGuestBook -> {
                    onNavigateToGuestBookDetail(intent.bookId)
                }
                is MessageIntent.ClickWriteAction -> {
                    // 우상단 ic_write 버튼을 눌렀을 때의 동작 처리부
                }
                else -> { /* 추가 처리 필요 시 작성 */ }
            }
        }

        // 3. 순수 Stateless UI 스크린에 바인딩
        MessageScreen(
            state = uiState,
            onIntent = onIntent
        )
    }
}