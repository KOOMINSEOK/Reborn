package com.gentlelady.reborn.feature.memorial_swipe.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.feature.memorial_swipe.MusicItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import com.gentlelady.reborn.core.theme.RebornBorderLightBlue
import com.gentlelady.reborn.ic_music
import com.gentlelady.reborn.ic_nav_home_default

@Composable
internal fun BoxScope.MemorialTopBar(
    musicItem: MusicItem?,
    onHomeClick: () -> Unit
) {
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
            .align(Alignment.TopCenter),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Floating player widget 컨테이너
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .height(56.dp) // 간격 확장에 맞춰 높이를 52dp -> 56dp로 미세 조정하여 밸런스 유지
                .clip(RoundedCornerShape(28.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .border(
                    border = BorderStroke(1.dp, RebornBorderLightBlue.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(28.dp)
                )
                // 시안의 컴포넌트 간 간격을 확보하기 위해 전체 좌우 패딩을 16dp로 확장
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. 회전하는 디스크 앨범 아트
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .rotate(rotation),
                shape = CircleShape,
                color = Color.DarkGray
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Art",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 9.sp,
                        modifier = Modifier.wrapContentSize(Alignment.Center)
                    )
                }
            }

            // 디스크와 텍스트 사이 간격을 넓게 조정
            Spacer(modifier = Modifier.width(14.dp))

            // 2. 순수 텍스트 영역 (제목과 가수 사이 간격 축소)
            Column(
                modifier = Modifier.weight(1f, fill = false), // 음표 아이콘과의 간격 확보를 위한 가변 너비 설정
                verticalArrangement = Arrangement.spacedBy(0.dp) // 간격을 완전히 좁혀 쫀쫀하게 결합
            ) {
                // 곡 제목 (14sp, 두꺼운 폰트 스타일 유지)
                Text(
                    text = musicItem?.title ?: "No Title",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // 아티스트 명 (11sp, 연한 회색 톤 유지)
                Text(
                    text = musicItem?.artist ?: "Unknown Artist",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 텍스트 영역과 우측 음표 아이콘 사이 간격을 넓게 조정
            Spacer(modifier = Modifier.width(16.dp))

            // 3. 음표 아이콘 (세로축 중앙 정렬 고정 및 시안 픽셀 매칭)
            Icon(
                painter = painterResource(Res.drawable.ic_music),
                contentDescription = "Music",
                tint = Color.White,
                modifier = Modifier
                    .size(16.dp) // 시안 가이드라인의 선명도에 맞춰 크기 최적화 (14dp -> 16dp)
                    .align(Alignment.CenterVertically)
            )
        }

        // 2. 홈 버튼
        IconButton(
            onClick = onHomeClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .border(
                    width = 1.dp,
                    color = RebornBorderLightBlue.copy(alpha = 0.4f),
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_nav_home_default),
                contentDescription = "Home",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MemorialTopBarPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.DarkGray)
        ) {
            MemorialTopBar(
                musicItem = MusicItem("See You Again", "Wiz Khalifa ft. Charlie Puth", ""),
                onHomeClick = {}
            )
        }
    }
}