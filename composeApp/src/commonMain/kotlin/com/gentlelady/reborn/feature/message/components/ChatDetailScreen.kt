package com.gentlelady.reborn.feature.message.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gentlelady.reborn.core.designsystem.components.RebornImage
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.message.presentation.ChatIntent
import com.gentlelady.reborn.message.presentation.ChatMessage
import com.gentlelady.reborn.message.presentation.ChatState
import com.gentlelady.reborn.message.presentation.ChatViewModel
import com.gentlelady.reborn.message.presentation.chatDayLabel
import org.koin.compose.viewmodel.koinViewModel

private val BubbleGray = Color(0xFFEDEFF2)

/** 대화창 진입점. [open] = 목록에서 연 방이면 Open, 프로필·검색에서 왔으면 OpenWith. 화면마다 ViewModel 하나 */
@Composable
internal fun ChatRoute(
    open: ChatIntent,
    onBack: () -> Unit,
    onPeerClick: (String) -> Unit,
    viewModel: ChatViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) { viewModel.onIntent(open) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    ChatDetailScreen(state, viewModel::onIntent, onBack, onPeerClick)
}

/** 1:1 대화창. 시안 '1:1 대화창 화면'. 카메라·음성·사진 보내기는 아직 없다 (글만) */
@Composable
internal fun ChatDetailScreen(
    state: ChatState,
    onIntent: (ChatIntent) -> Unit,
    onBack: () -> Unit,
    onPeerClick: (String) -> Unit,
) {
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            onIntent(ChatIntent.MessageShown)
        }
    }
    Scaffold(
        containerColor = Color.White,
        topBar = {
            ChatTopBar(
                title = state.peer?.name.orEmpty(),
                avatarUrl = state.peer?.avatarUrl,
                onBack = onBack,
                onTitleClick = { state.peer?.let { onPeerClick(it.userId) } }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                when {
                    state.isLoading -> CircularProgressIndicator(color = RebornCobaltBlue)
                    state.loadError != null -> Text(state.loadError.orEmpty(), color = RebornSlateGray, fontSize = 14.sp)
                    state.messages.isEmpty() -> Text("첫 메시지를 보내 보세요.", color = RebornSlateGray, fontSize = 14.sp)
                    else -> MessageList(state, onIntent, state.peer?.avatarUrl)
                }
            }
            if (state.loadError == null) {
                ChatInputBar(
                    value = state.input,
                    enabled = state.peer != null,
                    onValueChange = { onIntent(ChatIntent.UpdateInput(it)) },
                    onSend = { onIntent(ChatIntent.Send) }
                )
            }
        }
    }
}

/** 최신이 아래. 거꾸로 깐 목록이라 처음부터 맨 아래가 보이고, 위 끝에 닿으면 더 오래된 걸 받는다 */
@Composable
private fun MessageList(state: ChatState, onIntent: (ChatIntent) -> Unit, peerAvatar: String?) {
    val listState = rememberLazyListState()
    val newestFirst = state.messages.asReversed()
    // 새 메시지가 오면 (맨 아래를 보고 있을 때만) 맨 아래로
    val newestId = newestFirst.firstOrNull()?.id
    LaunchedEffect(newestId) { if (listState.firstVisibleItemIndex <= 1) listState.animateScrollToItem(0) }
    val nearTop by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1 } }
    LaunchedEffect(nearTop) { if (nearTop) onIntent(ChatIntent.LoadOlder) }

    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        newestFirst.forEachIndexed { i, msg ->
            item(key = msg.id) { MessageBubble(msg, peerAvatar, onRetry = { onIntent(ChatIntent.Retry(msg.id)) }) }
            // 날짜가 바뀌는 곳(이 메시지가 그날의 첫 메시지)에 구분선
            val day = chatDayLabel(msg.createdAt)
            val olderDay = newestFirst.getOrNull(i + 1)?.let { chatDayLabel(it.createdAt) }
            if (day.isNotEmpty() && day != olderDay) item(key = "day-${msg.id}") { DayDivider(day) }
        }
        if (state.isLoadingOlder) item(key = "older") {
            Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = RebornCobaltBlue)
            }
        }
    }
}

@Composable
private fun ChatTopBar(title: String, avatarUrl: String?, onBack: () -> Unit, onTitleClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "뒤로", tint = Color.Black)
            }
            Row(
                Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).clickable(onClick = onTitleClick).padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(avatarUrl, 32)
                Spacer(Modifier.width(8.dp))
                Text(
                    title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black,
                    maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false)
                )
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size(18.dp))
            }
            // ponytail: 시안의 예약 버튼 — 기능이 정해지면 연결 (지금은 표시만)
            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.Black, modifier = Modifier.padding(12.dp))
        }
        HorizontalDivider(color = RebornDividerGray, thickness = 1.dp)
    }
}

@Composable
private fun Avatar(url: String?, size: Int) {
    val modifier = Modifier.size(size.dp).clip(CircleShape)
    if (url.isNullOrBlank()) {
        Box(modifier.background(RebornDividerGray), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Person, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size((size * 0.6).dp))
        }
    } else {
        RebornImage(url = url, fallback = null, contentDescription = null, modifier = modifier)
    }
}

@Composable
private fun DayDivider(label: String) {
    Box(Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
        Surface(shape = RoundedCornerShape(12.dp), color = BubbleGray) {
            Text(label, fontSize = 11.sp, color = RebornSlateGray, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
        }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessage, peerAvatar: String?, onRetry: () -> Unit) {
    val arrangement = if (msg.isMine) Arrangement.End else Arrangement.Start
    Row(
        Modifier
            .fillMaxWidth()
            .alpha(if (msg.pending || msg.failed) 0.5f else 1f)
            .then(if (msg.failed) Modifier.clickable(onClick = onRetry) else Modifier),
        horizontalArrangement = arrangement,
        verticalAlignment = Alignment.Bottom
    ) {
        if (msg.failed) {
            Text("전송 실패 · 다시 보내기", fontSize = 11.sp, color = Color.Red, modifier = Modifier.padding(end = 6.dp))
        }
        if (!msg.isMine) {
            if (msg.showAvatar) Avatar(peerAvatar, 28) else Spacer(Modifier.width(28.dp))
            Spacer(Modifier.width(6.dp))
        }
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (msg.isMine) RebornCobaltBlue else BubbleGray,
            modifier = Modifier.widthIn(max = 260.dp)
        ) {
            Text(
                text = msg.text,
                fontSize = 14.sp,
                color = if (msg.isMine) Color.White else Color.Black,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun ChatInputBar(value: String, enabled: Boolean, onValueChange: (String) -> Unit, onSend: () -> Unit) {
    HorizontalDivider(color = RebornDividerGray, thickness = 1.dp)
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ponytail: 사진·음성 보내기는 아직 — 시안 모양만
        Surface(shape = CircleShape, color = RebornCobaltBlue, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
        }
        Spacer(Modifier.width(8.dp))
        Surface(
            modifier = Modifier.weight(1f).heightIn(min = 40.dp),
            shape = RoundedCornerShape(20.dp),
            color = BubbleGray
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) Text("메시지 보내기...", fontSize = 14.sp, color = RebornSlateGray)
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        enabled = enabled,
                        maxLines = 5,
                        textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                        cursorBrush = SolidColor(RebornCobaltBlue),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.width(8.dp))
                if (value.isBlank()) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Icon(Icons.Default.Add, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size(20.dp))
                } else {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "전송",
                        tint = RebornCobaltBlue,
                        modifier = Modifier.size(22.dp).clickable(onClick = onSend)
                    )
                }
            }
        }
    }
}
