package com.gentlelady.reborn.feature.message

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.gentlelady.reborn.data.MockDataSource
import com.gentlelady.reborn.feature.message.components.ChatDetailScreen
import com.gentlelady.reborn.feature.message.components.GuestBookSearchScreen
import com.gentlelady.reborn.feature.message.components.MessageSuggestScreen
import com.gentlelady.reborn.message.presentation.MessageIntent
import com.gentlelady.reborn.message.presentation.MessageState
import com.gentlelady.reborn.message.presentation.MessageTab

/**
 * 메시지 피처 네비게이션.
 * 메인(message) + 검색 화면 2종 + 1:1 대화창을 등록한다.
 * 검색/대화창 화면은 하단 탭이 없는 전체 화면이며 로컬 상태로만 동작한다(프로토타입).
 */
fun NavGraphBuilder.messageNavGraph(
    navController: NavHostController,
    state: MessageState,
    onIntent: (MessageIntent) -> Unit
) {
    composable("message") {
        MessageScreen(
            state = state,
            onIntent = { intent ->
                when (intent) {
                    is MessageIntent.ClickSearchBar -> navController.navigate(
                        if (state.currentTab == MessageTab.MESSAGE) "message/search"
                        else "message/guestbook_search"
                    )
                    is MessageIntent.ClickChatRoom -> navController.navigate("message/chat/${intent.roomId}")
                    is MessageIntent.ClickGuestBook -> {
                        // TODO(stub): 내가 남긴 방명록의 추모 페이지 방명록 화면으로 이동
                    }
                    else -> onIntent(intent)
                }
            }
        )
    }

    composable("message/search") {
        MessageSuggestScreen(
            suggested = MockDataSource.messageSuggestedUsers,
            onBack = { navController.popBackStack() },
            onUserClick = {
                // TODO(stub): 해당 사용자 프로필 화면으로 이동
            }
        )
    }

    composable("message/guestbook_search") {
        GuestBookSearchScreen(
            all = MockDataSource.messageGuestBooks,
            onBack = { navController.popBackStack() },
            onResultClick = {
                // TODO(stub): 내 댓글이 있는 추모 페이지 방명록 화면으로 이동
            }
        )
    }

    composable("message/chat/{roomId}") { backStackEntry ->
        val roomId = backStackEntry.arguments?.getString("roomId").orEmpty()
        val room = MockDataSource.messageChatRooms.firstOrNull { it.id == roomId }
        ChatDetailScreen(
            title = room?.name ?: "대화",
            initialMessages = MockDataSource.chatConversationOf(roomId),
            onBack = { navController.popBackStack() }
        )
    }
}
