// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/memorial/components/MemorialGuestBookList.kt
package com.gentlelady.reborn.feature.memorial.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.ic_arrow_up
import com.gentlelady.reborn.memorial.presentation.MemorialGuestBookItem
import com.gentlelady.reborn.memorial.presentation.MemorialOwnerType
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MemorialGuestBookList(
    guestBookMessages: List<MemorialGuestBookItem>,
    inputText: String,
    ownerType: MemorialOwnerType,
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "남겨진 방명록이 없습니다.\n첫 마음을 전해 보세요.",
                    color = RebornSlateGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 12.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = if (ownerType == MemorialOwnerType.OTHER_MEMORIAL) 80.dp else 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(guestBookMessages) { item ->
                    GuestBookMessageRow(item = item)
                }
            }
        }

        // 2. 타인 관점 페이지일 때만 하단 방명록 입력 바 표시 (시안 1 요구사항)
        if (ownerType == MemorialOwnerType.OTHER_MEMORIAL) {
            GuestBookInputBottomBar(
                inputText = inputText,
                onInputTextChange = onInputTextChange,
                onSubmitClick = onSubmitClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

/**
 * 방명록 개별 메시지 아이템
 */
@Composable
private fun GuestBookMessageRow(
    item: MemorialGuestBookItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 작성자 아바타
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = RebornDividerGray
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = item.authorName.take(1),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = RebornSlateGray
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            // 말풍선 박스
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = RebornBackgroundGray,
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    text = item.message,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 작성 시간
            Text(
                text = item.timestamp,
                fontSize = 11.sp,
                color = RebornSlateGray,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

/**
 * 하단 방명록 입력 바 (ic_arrow_up.xml 전송 아이콘 적용)
 */
@Composable
private fun GuestBookInputBottomBar(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // 캡슐 형태 컨테이너
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
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
                            painter = painterResource(Res.drawable.ic_arrow_up),
                            contentDescription = "전송",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
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
        MemorialGuestBookItem("2", "민수", null, "보고싶어 ♥", "어제, 4:15 PM")
    )

    MaterialTheme {
        Surface {
            MemorialGuestBookList(
                guestBookMessages = dummyMessages,
                inputText = "메시지 작성 중...",
                ownerType = MemorialOwnerType.OTHER_MEMORIAL,
                onInputTextChange = {},
                onSubmitClick = {}
            )
        }
    }
}