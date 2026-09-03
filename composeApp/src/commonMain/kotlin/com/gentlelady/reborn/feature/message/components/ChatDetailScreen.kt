package com.gentlelady.reborn.feature.message.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.message.presentation.ChatMessage
import org.jetbrains.compose.ui.tooling.preview.Preview

private val BubbleGray = Color(0xFFEDEFF2)

/**
 * 1:1 대화창. 시안 '1:1 대화창 화면'.
 * 전송은 로컬 목록에 즉시 추가(데모용). 우상단 달력 아이콘은 표시만.
 */
@Composable
internal fun ChatDetailScreen(
    title: String,
    initialMessages: List<ChatMessage>,
    onBack: () -> Unit
) {
    val messages = remember { mutableStateListOf<ChatMessage>().apply { addAll(initialMessages) } }
    var input by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.White,
        topBar = { ChatTopBar(title = title, onBack = onBack) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item { DayDivider("오늘") }
                items(messages, key = { it.id }) { msg -> MessageBubble(msg) }
            }
            ChatInputBar(
                value = input,
                onValueChange = { input = it },
                onSend = {
                    val t = input.trim()
                    if (t.isNotEmpty()) {
                        messages.add(ChatMessage(id = "local_${messages.size}", text = t, isMine = true))
                        input = ""
                    }
                }
            )
        }
    }
}

@Composable
private fun ChatTopBar(title: String, onBack: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "뒤로", tint = Color.Black)
            }
            Box(Modifier.size(32.dp).background(RebornDividerGray, CircleShape))
            Spacer(Modifier.width(8.dp))
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size(18.dp))
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { /* 표시만: 동작 없음 */ }) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "예약", tint = Color.Black)
            }
        }
        HorizontalDivider(color = RebornDividerGray, thickness = 1.dp)
    }
}

@Composable
private fun DayDivider(label: String) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Surface(shape = RoundedCornerShape(12.dp), color = BubbleGray) {
            Text(label, fontSize = 11.sp, color = RebornSlateGray, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
        }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessage) {
    val arrangement = if (msg.isMine) Arrangement.End else Arrangement.Start
    Row(Modifier.fillMaxWidth(), horizontalArrangement = arrangement, verticalAlignment = Alignment.Bottom) {
        if (!msg.isMine) {
            if (msg.showAvatar) Box(Modifier.size(28.dp).background(RebornDividerGray, CircleShape))
            else Spacer(Modifier.width(28.dp))
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
private fun ChatInputBar(value: String, onValueChange: (String) -> Unit, onSend: () -> Unit) {
    HorizontalDivider(color = RebornDividerGray, thickness = 1.dp)
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = CircleShape, color = RebornCobaltBlue, modifier = Modifier.size(36.dp)) {
            Icon(
                Icons.Default.PhotoCamera, contentDescription = "카메라", tint = Color.White,
                modifier = Modifier.padding(8.dp)
            )
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
                        singleLine = true,
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
                        modifier = Modifier.size(20.dp).clickable(onClick = onSend)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChatDetailScreenPreview() {
    MaterialTheme {
        ChatDetailScreen(
            title = "김민수",
            initialMessages = listOf(
                ChatMessage("1", "너 오늘 어디야", isMine = true),
                ChatMessage("2", "뭐야", isMine = false),
                ChatMessage("3", "아 ㅋㅋ 그치 근데 그건 좀;;", isMine = false, showAvatar = true)
            ),
            onBack = {}
        )
    }
}
