package com.gentlelady.reborn.feature.message

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.core.designsystem.MessageCard.RebornChatRow
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.feature.message.components.MessageTopAppBar
import com.gentlelady.reborn.message.presentation.ChatRoomItem
import com.gentlelady.reborn.message.presentation.GuestBookItem
import com.gentlelady.reborn.message.presentation.MessageIntent
import com.gentlelady.reborn.message.presentation.MessageState
import com.gentlelady.reborn.message.presentation.MessageTab
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MessageScreen(
    state: MessageState,
    onIntent: (MessageIntent) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0.dp), // 바깥 MainScreen Scaffold가 이미 하단 인셋을 처리하므로 중복 방지
        topBar = {
            MessageTopAppBar(
                searchQuery = state.searchQuery,
                currentTab = state.currentTab,
                onSearchQueryChange = { onIntent(MessageIntent.UpdateSearchQuery(it)) },
                onTabSelect = { onIntent(MessageIntent.SelectTab(it)) },
                onWriteClick = { onIntent(MessageIntent.ClickWriteAction) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state.currentTab) {
                MessageTab.MESSAGE -> {
                    itemsIndexed(state.chatRooms) { index, room ->
                        RebornChatRow(
                            name = room.name,
                            content = room.lastMessage,
                            timestamp = room.timestamp,
                            isUnread = room.isUnread,
                            isMemorialMode = false,
                            onClick = { onIntent(MessageIntent.ClickChatRoom(room.id)) }
                        )
                        HorizontalDivider(color = RebornDividerGray, thickness = 1.dp)
                    }
                }
                MessageTab.GUEST_BOOK -> {
                    itemsIndexed(state.guestBooks) { index, book ->
                        RebornChatRow(
                            name = book.deceasedName,
                            content = book.recentContent,
                            timestamp = book.relativeTime,
                            isUnread = false,
                            isMemorialMode = true,
                            onClick = { onIntent(MessageIntent.ClickGuestBook(book.id)) }
                        )
                        HorizontalDivider(color = RebornDividerGray, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

// 프리뷰 규칙 준수: 가벼운 하드코딩 직접 주입 (Direct Injection) 적용
@Preview
@Composable
private fun MessageScreenMessageTabPreview() {
    val dummyRooms = listOf(
        ChatRoomItem(id = "1", name = "김민수", lastMessage = "아 ㅋㅋ 그치 근데 그건 좀;;", timestamp = "2h", isUnread = true),
        ChatRoomItem(id = "2", name = "박지연", lastMessage = "나? 집이지", timestamp = "5h", isUnread = true),
        ChatRoomItem(id = "3", name = "이준호", lastMessage = "2시간 전 보냄", timestamp = "Yesterday", isUnread = false)
    )
    val previewState = MessageState(
        currentTab = MessageTab.MESSAGE,
        chatRooms = dummyRooms
    )
    MaterialTheme {
        Surface {
            MessageScreen(state = previewState, onIntent = {})
        }
    }
}

@Preview
@Composable
private fun MessageScreenGuestBookTabPreview() {
    val dummyBooks = listOf(
        GuestBookItem(id = "1", deceasedName = "김영희", recentContent = "영원히 기억하겠습니다. 하늘에서 ...", relativeTime = "1년 전"),
        GuestBookItem(id = "2", deceasedName = "이철수", recentContent = "당신의 따뜻한 미소가 그립습니다...", relativeTime = "8개월 전")
    )
    val previewState = MessageState(
        currentTab = MessageTab.GUEST_BOOK,
        guestBooks = dummyBooks
    )

    MaterialTheme {
        Surface {
            MessageScreen(state = previewState, onIntent = {})
        }
    }
}