// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/memorial/guestbook/MemorialGuestBookList.kt
package com.gentlelady.reborn.feature.memorial.guestbook

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.ic_comment
import com.gentlelady.reborn.ic_send
import com.gentlelady.reborn.memorial.presentation.MemorialGuestBookItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MemorialGuestBookList(
    guestBookMessages: List<MemorialGuestBookItem>,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. 방명록 목록 리스트 (Scrollable)
        if (guestBookMessages.isEmpty()) {
            GuestBookEmptyState(modifier = Modifier.fillMaxSize().padding(bottom = 72.dp))
        } else {
            val listState = rememberLazyListState()

            // 새 방명록이 맨 아래에 추가될 때마다 그 위치로 자동 스크롤
            LaunchedEffect(guestBookMessages.size) {
                listState.animateScrollToItem(guestBookMessages.lastIndex)
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 12.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(guestBookMessages) { item ->
                    GuestBookMessageRow(item = item)
                }
            }
        }

        // 2. 하단 방명록 입력 바 (내 메모리얼/타인 메모리얼 구분 없이 항상 표시)
        GuestBookInputBottomBar(
            inputText = inputText,
            onInputTextChange = onInputTextChange,
            onSubmitClick = onSubmitClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * 방명록이 하나도 없을 때 보여주는 빈 상태 화면
 */
@Composable
private fun GuestBookEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(color = RebornBackgroundGray, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_comment),
                contentDescription = null,
                tint = RebornSlateGray,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "아직 작성된 방명록이 없습니다.",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "가장 먼저 따뜻한 마음을 남겨보세요.",
            fontSize = 13.sp,
            color = RebornSlateGray,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 방명록 개별 메시지 아이템. 내가 남긴 메시지(authorName == "나")는 연한 파랑 말풍선으로 오른쪽 정렬,
 * 그 외에는 연한 회색 말풍선으로 왼쪽 정렬되는 1:1 채팅형 UI. 날짜는 말풍선 옆(바깥쪽)에 표시한다.
 */
@Composable
private fun GuestBookMessageRow(
    item: MemorialGuestBookItem,
    modifier: Modifier = Modifier
) {
    val isMine = item.authorName == "나"

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (isMine) {
            Text(text = item.timestamp, fontSize = 11.sp, color = RebornSlateGray)
            Spacer(modifier = Modifier.width(6.dp))
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isMine) RebornMyBubbleBg else RebornOtherBubbleBg,
            modifier = Modifier.widthIn(max = 260.dp)
        ) {
            Text(
                text = item.message,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                lineHeight = 20.sp
            )
        }

        if (!isMine) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = item.timestamp, fontSize = 11.sp, color = RebornSlateGray)
        }
    }
}

/**
 * 하단 방명록 입력 바 (ic_send.xml 전송 아이콘 적용)
 */
@Composable
private fun GuestBookInputBottomBar(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 캡슐(타원) 형태 입력창만 단독으로 표시 (바깥 사각 박스/그림자 없음)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .height(44.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, RebornDividerGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 입력창 & 힌트
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (inputText.isEmpty()) {
                    Text(
                        text = "방명록을 남겨보세요! (글자수 100자 제한)",
                        fontSize = 12.sp,
                        color = RebornSlateGray
                    )
                }
                BasicTextField(
                    value = inputText,
                    onValueChange = { if (it.length <= 100) onInputTextChange(it) },
                    textStyle = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Black
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSubmitClick() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 전송 원형 버튼
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = if (inputText.isNotBlank()) RebornDeepBlue else RebornUnselectedGray,
                        shape = CircleShape
                    )
                    .clickable(
                        enabled = inputText.isNotBlank(),
                        onClick = onSubmitClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_send),
                    contentDescription = "전송",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// Direct Injection 프리뷰 규칙 준수
@Preview
@Composable
private fun MemorialGuestBookListOtherViewPreview() {
    val dummyMessages = listOf(
        MemorialGuestBookItem("1", "지연", null, "오늘따라 너 생각이 나니 ㅋㅋ 잘 지내지?", "오늘, 10:30 AM"),
        MemorialGuestBookItem("2", "나", null, "보고싶어 ♥", "어제, 4:15 PM")
    )

    MaterialTheme {
        Surface {
            MemorialGuestBookList(
                guestBookMessages = dummyMessages,
                inputText = "메시지 작성 중...",
                onInputTextChange = {},
                onSubmitClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun MemorialGuestBookListEmptyPreview() {
    MaterialTheme {
        Surface {
            MemorialGuestBookList(
                guestBookMessages = emptyList(),
                inputText = "",
                onInputTextChange = {},
                onSubmitClick = {}
            )
        }
    }
}
