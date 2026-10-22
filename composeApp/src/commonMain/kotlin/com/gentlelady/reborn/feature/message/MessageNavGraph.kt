package com.gentlelady.reborn.feature.message

import androidx.compose.runtime.LaunchedEffect
import com.gentlelady.reborn.feature.message.components.ChatRoute
import com.gentlelady.reborn.feature.message.components.MessageSuggestRoute
import com.gentlelady.reborn.message.presentation.ChatIntent

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
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
        // 들어올 때마다 대화방 목록을 새로 (대화창에서 보낸·받은 메시지 반영)
        LaunchedEffect(Unit) { onIntent(MessageIntent.Refresh) }
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

    // 메시지 보낼 사람 찾기 → 누르면 그 사람과의 대화창
    composable("message/search") {
        MessageSuggestRoute(
            onBack = { navController.popBackStack() },
            onUserClick = { user -> navController.navigate("message/dm/${user.id}") { popUpTo("message/search") { inclusive = true } } }
        )
    }

    composable("message/guestbook_search") {
        GuestBookSearchScreen(
            all = state.guestBooks,
            onBack = { navController.popBackStack() },
            onResultClick = { navController.openGuestBook(it) }
        )
    }

    // 목록에서 연 대화방
    composable("message/chat/{roomId}") { backStackEntry ->
        ChatRoute(
            open = ChatIntent.Open(backStackEntry.arguments?.getString("roomId").orEmpty()),
            onBack = { navController.popBackStack() },
            onPeerClick = { navController.navigate("user/$it") }
        )
    }

    // 그 사람과 대화 시작 (프로필의 메시지 버튼·사람 찾기) — 대화방이 없으면 서버가 만든다
    composable("message/dm/{userId}") { backStackEntry ->
        ChatRoute(
            open = ChatIntent.OpenWith(backStackEntry.arguments?.getString("userId").orEmpty()),
            onBack = { navController.popBackStack() },
            onPeerClick = { navController.navigate("user/$it") }
        )
    }
}

/** 내가 남긴 방명록 → 그 추모 페이지의 방명록 탭을 이 글 위치로 (목업 항목은 추모 페이지가 없어 이동하지 않음) */
private fun NavHostController.openGuestBook(book: GuestBookItem) {
    if (book.memorialId.isBlank()) return
    navigate("memorial/other/${book.memorialId}?focusGuestbook=${book.id}")
}
