package com.gentlelady.reborn.feature.message

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.gentlelady.reborn.data.MockDataSource
import com.gentlelady.reborn.feature.message.components.ChatDetailScreen
import com.gentlelady.reborn.feature.message.components.GuestBookSearchScreen
import com.gentlelady.reborn.feature.message.components.MessageSuggestScreen
import com.gentlelady.reborn.message.presentation.GuestBookItem
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
                    is MessageIntent.ClickGuestBook ->
                        state.guestBooks.firstOrNull { it.id == intent.bookId }?.let { navController.openGuestBook(it) }
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
            all = state.guestBooks,
            onBack = { navController.popBackStack() },
            onResultClick = { navController.openGuestBook(it) }
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

/** 내가 남긴 방명록 → 그 추모 페이지의 방명록 탭을 이 글 위치로 (목업 항목은 추모 페이지가 없어 이동하지 않음) */
private fun NavHostController.openGuestBook(book: GuestBookItem) {
    if (book.memorialId.isBlank()) return
    navigate("memorial/other/${book.memorialId}?focusGuestbook=${book.id}")
}
