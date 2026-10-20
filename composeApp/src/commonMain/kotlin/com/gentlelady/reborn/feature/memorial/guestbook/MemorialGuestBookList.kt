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
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
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
    modifier: Modifier = Modifier,
    isSubmitting: Boolean = false,
    error: String? = null,
    hasOlder: Boolean = false,
    isLoadingOlder: Boolean = false,
    onLoadOlder: () -> Unit = {},
    hasNewer: Boolean = false,
    isLoadingNewer: Boolean = false,
    onLoadNewer: () -> Unit = {},
    /** 이 방명록 위치로 스크롤하고 잠깐 강조 (내 방명록에서 눌러 들어왔을 때) */
    focusId: String? = null,
    /** 바뀔 때마다 위치를 다시 잡는다: 포커스 글, 없으면 맨 아래(최신) */
    scrollToken: Int = 0,
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
            val latestOnLoadOlder by rememberUpdatedState(onLoadOlder)
            val latestOnLoadNewer by rememberUpdatedState(onLoadNewer)

            // 사용자가 손으로 한 번이라도 끌었나 — 자동 스크롤(처음 열기·새 글)이 이전 글 불러오기를 일으키지 않게
            var userDragged by remember { mutableStateOf(false) }
            val isDragged by listState.interactionSource.collectIsDraggedAsState()
            LaunchedEffect(isDragged) { if (isDragged) userDragged = true }

            // 위치 잡기 (토큰이 바뀔 때만 — 이전/이후 글을 이어 붙일 땐 안 움직인다):
            // 포커스 글이 있으면 그 글이 위에서 세 번째쯤 오게 + 잠깐 강조, 없으면 맨 아래(최신).
            // 대상은 지금 목록 기준으로 계산 (layoutInfo 는 아직 이전 측정값일 수 있다)
            var highlightedId by remember { mutableStateOf<String?>(null) }
            LaunchedEffect(scrollToken) {
                val focusIndex = focusId?.let { id -> guestBookMessages.indexOfFirst { it.id == id } } ?: -1
                when {
                    focusIndex >= 0 -> {
                        listState.scrollToItem((focusIndex - 2).coerceAtLeast(0))
                        highlightedId = focusId
                        delay(HIGHLIGHT_MILLIS)
                        highlightedId = null
                    }
                    scrollToken > 1 -> listState.animateScrollToItem(guestBookMessages.lastIndex)
                    else -> listState.scrollToItem(guestBookMessages.lastIndex)
                }
            }

            // 이전 글이 위에 붙으면 보던 글(직전의 맨 위 글)로 되돌려 둔다 → 읽던 위치 유지,
            // 그리고 다시 위로 스크롤해야 다음 페이지를 부른다. (내가 이전 글을 부른 경우에만)
            var expectingOlder by remember { mutableStateOf(false) }
            var prevFirstId by remember { mutableStateOf<String?>(null) }
            LaunchedEffect(guestBookMessages.first().id) {
                val prev = prevFirstId
                prevFirstId = guestBookMessages.first().id
                if (prev == null || !expectingOlder) return@LaunchedEffect
                expectingOlder = false
                val index = guestBookMessages.indexOfFirst { it.id == prev }
                if (index > 0) listState.scrollToItem(index)
            }

            // 손으로 스크롤해서 맨 아래에 닿으면 이후 글 불러오기 (특정 글로 이동해 들어와 중간을 보고 있을 때만).
            // 뒤에 붙는 거라 보던 위치는 그대로다.
            val latestIsLoadingNewer by rememberUpdatedState(isLoadingNewer)
            LaunchedEffect(listState, hasNewer) {
                if (!hasNewer) return@LaunchedEffect
                snapshotFlow { userDragged && !latestIsLoadingNewer && !listState.canScrollForward }
                    .distinctUntilChanged()
                    .filter { it }
                    .collect { latestOnLoadNewer() }
            }

            // 손으로 스크롤해서 정말 맨 위(첫 글이 다 보임)에 닿으면 이전 글 불러오기.
            // 불러오는 중엔 안 부르고, 실패하면 사용자가 내렸다 다시 올려야 재시도 (자동 재요청 루프 방지)
            val latestIsLoadingOlder by rememberUpdatedState(isLoadingOlder)
            LaunchedEffect(listState, hasOlder) {
                if (!hasOlder) return@LaunchedEffect
                snapshotFlow {
                    userDragged && !latestIsLoadingOlder &&
                        listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
                }
                    .distinctUntilChanged()
                    .filter { it }
                    .collect {
                        expectingOlder = true
                        latestOnLoadOlder()
                    }
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
                items(guestBookMessages, key = { it.id }) { item ->
                    GuestBookMessageRow(item = item, highlighted = item.id == highlightedId)
                }
            }

            // 로딩 표시는 목록 항목이 아니라 위에 겹쳐 그린다 — 항목으로 넣으면 인덱스가 흔들려 재요청을 부른다
            if (isLoadingOlder) {
                CircularProgressIndicator(
                    color = RebornDeepBlue,
                    strokeWidth = 2.dp,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp).size(20.dp)
                )
            }
            if (isLoadingNewer) {
                CircularProgressIndicator(
                    color = RebornDeepBlue,
                    strokeWidth = 2.dp,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 72.dp).size(20.dp)
                )
            }
        }

        // 2. 하단 방명록 입력 바 (내 메모리얼/타인 메모리얼 구분 없이 항상 표시)
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            error?.let { message ->
                Text(
                    text = message,
                    fontSize = 12.sp,
                    color = RebornDangerRed,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            GuestBookInputBottomBar(
                inputText = inputText,
                onInputTextChange = onInputTextChange,
                onSubmitClick = onSubmitClick,
                canSubmit = inputText.isNotBlank() && !isSubmitting
            )
        }
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
    modifier: Modifier = Modifier,
    /** 내 방명록에서 눌러 들어온 그 글 — 잠깐 테두리로 강조 */
    highlighted: Boolean = false,
) {
    val isMine = item.isMine

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
            border = if (highlighted) BorderStroke(2.dp, RebornCobaltBlue) else null,
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
    canSubmit: Boolean,
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
                    keyboardActions = KeyboardActions(onSend = { if (canSubmit) onSubmitClick() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 전송 원형 버튼
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = if (canSubmit) RebornDeepBlue else RebornUnselectedGray,
                        shape = CircleShape
                    )
                    .clickable(
                        enabled = canSubmit,
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
        MemorialGuestBookItem("2", "나", null, "보고싶어 ♥", "어제, 4:15 PM", isMine = true)
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

private const val HIGHLIGHT_MILLIS = 2500L
