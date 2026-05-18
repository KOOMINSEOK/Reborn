package com.gentlelady.reborn.feature.memorial_swipe.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.feature.memorial_swipe.MusicItem
import org.jetbrains.compose.ui.tooling.preview.Preview

// 컴포넌트 분화 및 하위 의존 컴포넌트는 internal로 은닉 규칙 준수[cite: 8, 32].
@Composable
internal fun BoxScope.MemorialTopBar(
    musicItem: MusicItem?,
    onHomeClick: () -> Unit
) {
    // 1. 음악 디스크의 무한 회전을 위한 360도 루프 애니메이션 정의 [cite: 79, 81]
    val infiniteTransition = rememberInfiniteTransition(label = "MusicDiscRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DiscRotationAngle"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .align(Alignment.TopCenter), // BoxScope 수신 객체를 통한 플로팅 정렬 [cite: 60, 68]
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 음악 정보 위젯 (좌상단 플로팅) [cite: 79, 81]
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black.copy(alpha = 0.6f)) // 몰입형 뷰를 위한 어두운 투명도 레이어
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 회전하는 디스크 앨범 아트 영역 (Spinning music disc) [cite: 79, 81]
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .rotate(rotation), // 계산된 회전 각도 실시간 반영
                shape = CircleShape,
                color = Color.LightGray // 동적 에셋 플레이스홀더 규칙 준수 [cite: 5, 58]
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Art",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.wrapContentSize(Alignment.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(musicItem?.title ?: "No Title", color = Color.White, fontSize = 14.sp)
                Text(musicItem?.artist ?: "Unknown Artist", color = Color.LightGray, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Filled.MusicNote,
                contentDescription = "Music",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        // 2. 홈 버튼 (우상단 플로팅) [cite: 79, 81]
        IconButton(
            onClick = onHomeClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)) // 완전 몰입형 테마에 맞추어 반투명(Ghost) 스타일 최적화 [cite: 79, 87]
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home",
                tint = Color.White, // 심플하고 직관적인 화이트 톤 피드백 적용
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// BoxScope 확장성 대응을 위한 단독 프리뷰 래퍼 구현 (지역 함수 사용 금지 원칙 준수) [cite: 9, 60]
@Preview
@Composable
private fun MemorialTopBarPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.DarkGray) // 시안의 어두운 풀 스크린 배경 시각화 레이어
        ) {
            MemorialTopBar(
                musicItem = MusicItem("See You Again", "Wiz Khalifa ft. Charlie Puth", ""),
                onHomeClick = {}
            )
        }
    }
}