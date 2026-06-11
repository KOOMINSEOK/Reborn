package com.gentlelady.reborn.core.designsystem.MessageCard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RebornChatRow(
    name: String,
    content: String,
    timestamp: String,
    modifier: Modifier = Modifier,
    isUnread: Boolean = false,            // 메시지 탭용: 안읽음 상태 분기
    isMemorialMode: Boolean = false,      // 방명록 탭용: 고인 모드 분기
    onClick: () -> Unit
) {
    // 시안 규칙: 첫 2개 스레드(안읽음)는 연한 블루 배경(RebornLightBlueBg), 읽은 상태는 흰색 배경
    val backgroundColor = if (isUnread && !isMemorialMode) RebornLightBlueBg else Color.White

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 아바타 영역 (방명록 모드일 경우 하단 캔들 배지 등을 결합할 수 있도록 Box 구성)
        Box(modifier = Modifier.size(48.dp)) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = Color.LightGray // 임시 에셋 플레이스홀더
            ) {
                // 원형 아바타 이미지 안착부
            }

            // 시안 규칙: 방명록(Memorial) 모드일 때 아바타 우하단 candle badge 등 indicator 분기 영역
            if (isMemorialMode) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(RebornDeepBlue, shape = CircleShape) // 임시 캔들 배지 형태
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 2. 텍스트 영역 (이름 + 최근 메시지 본문)
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 시안 규칙: 방명록 모드일 경우 이름 앞에 故(고) Prefix 명시 규칙 준수
                val displayName = if (isMemorialMode) "故 $name" else name

                Text(
                    text = displayName,
                    fontSize = 15.sp,
                    // 시안 규칙: 안읽은 스레드는 bold text, 읽은 스레드는 일반 폰트
                    fontWeight = if (isUnread && !isMemorialMode) FontWeight.Bold else FontWeight.Medium,
                    color = Color.Black
                )

                if (isMemorialMode) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "🌸", fontSize = 12.sp) // 시안 규칙: 이름 옆 🌸 flower indicator 기재
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = content,
                fontSize = 13.sp,
                // 시안 규칙: 읽은 스레드는 muted text(SlateGray)
                color = if (isUnread && !isMemorialMode) Color.Black else RebornSlateGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 3. 메타 데이터 영역 (시간 + 알림 도트)
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = timestamp,
                fontSize = 11.sp,
                color = RebornSlateGray // 시ated-400 혹은 SlateGray에 대응하는 세맨틱 컬러 적용
            )

            if (isUnread && !isMemorialMode) {
                Spacer(modifier = Modifier.height(6.dp))
                // 시안 규칙: 안읽음 상태일 때 cobalt dot 표시
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(RebornCobaltBlue, shape = CircleShape)
                )
            }
        }
    }
}

// 프리뷰 규칙: Crash 방지를 위해 Provider/MockDataSource 없이 가벼운 하드코딩 직접 주입(Direct Injection)
@Preview
@Composable
private fun RebornChatRowMessagePreview() {
    MaterialTheme {
        Surface {
            RebornChatRow(
                name = "김민수",
                content = "아 ㅋㅋ 그치 근데 그건 좀;;",
                timestamp = "2h",
                isUnread = true,
                isMemorialMode = false,
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun RebornChatRowMemorialPreview() {
    MaterialTheme {
        Surface {
            RebornChatRow(
                name = "김영희",
                content = "영원히 기억하겠습니다. 하늘에서 ...",
                timestamp = "1년 전",
                isUnread = false,
                isMemorialMode = true,
                onClick = {}
            )
        }
    }
}