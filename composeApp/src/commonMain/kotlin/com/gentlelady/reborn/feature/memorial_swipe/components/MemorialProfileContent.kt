package com.gentlelady.reborn.feature.memorial_swipe.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.RebornBorderLightBlue
import com.gentlelady.reborn.feature.memorial_swipe.MemorialItem
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import org.jetbrains.compose.resources.painterResource

import com.gentlelady.reborn.ic_memorial_ribbon
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ColumnScope.MemorialProfileContent(item: MemorialItem) {
    // 1. [특성 적용] 리본 배지 뒤에서 은은하게 번지는 Glow(빛남) 무한 애니메이션 정의 [cite: 79, 80]
    val infiniteTransition = rememberInfiniteTransition(label = "RibbonGlowTransition")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.5f, // 리본 배지 바깥으로 빛이 퍼져나가는 크기
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowScaleValue"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f, // 바깥으로 갈수록 투명하게 소멸
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlphaValue"
    )

    Column(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. [특성 최적화] 빛나는 효과가 결합된 코발트 블루 리본 배지
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(64.dp) // 애니메이션 잔상이 표현될 프레임 영역 확보
        ) {
            // 1. 아우라 효과 레이어 (요청하신대로 RebornDeepBlue 적용)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(glowScale)
                    .alpha(glowAlpha)
                    // 아키텍처 규칙 준수: 지정해주신 RebornDeepBlue 상수를 바인딩
                    .background(RebornDeepBlue, shape = CircleShape)
            )

            // 2. 메인 리본 배지 표면 (Figma 기준 48x48 규격 반영)
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    // 시안의 외곽 테두리선은 RebornBorderLightBlue 상수를 활용해 표현
                    .border(
                        width = 2.dp,
                        color = RebornBorderLightBlue,
                        shape = CircleShape
                    ),
                shape = CircleShape,
                color = RebornDeepBlue // 아이콘 배경색을 RebornDeepBlue로 매칭
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        // painterResource와 Res 클래스를 활용해 xml 에셋 로드
                        painter = painterResource(Res.drawable.ic_memorial_ribbon),
                        contentDescription = "Ribbon",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(2.dp) // 내부 아이콘 크기 밸런스 조정
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 2. 생몰년도 날짜 태그 [cite: 51]
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(alpha = 0.5f)) // 몰입형 뷰를 위한 어두운 가이드 투명도 반영
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${item.birthDate} — ${item.deathDate}",
                color = Color.White,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // 3. 원형 프로필 이미지 (하얀색 외곽선 테두리 하이라이트) [cite: 51]
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .border(width = 4.dp, color = Color.White, shape = CircleShape) // 시안의 흰색 테두리 준수 [cite: 51]
        ) {
            // 동적 에셋 플레이스홀더 사용 규칙 준수. [cite: 5]
            Surface(modifier = Modifier.fillMaxSize(), shape = CircleShape, color = Color.LightGray) {
                Text("Profile", color = Color.White, modifier = Modifier.wrapContentSize(Alignment.Center))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // 4. 이름 및 세부 직업/지역 정보 텍스트 [cite: 51]
        Text(text = item.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "${item.jobTitle} , ${item.location}", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
    }
}

// ColumnScope 확장 함수 단독 렌더링 검증용 프리뷰 래퍼 (지역 함수 선언 금지 규칙 준수) [cite: 9, 60]
@Preview
@Composable
private fun MemorialProfileContentPreview() {
    val mockItem = MemorialItem(
        id = "1",
        name = "김첨지",
        jobTitle = "소방관",
        location = "서울특별시",
        birthDate = "1987.03.02",
        deathDate = "2024.01.03",
        profileImageUrl = "",
        backgroundImageUrl = "",
        currentMusic = null
    )
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .background(Color.DarkGray) // 시안의 몰입형 분위기 시각화용 가상 어두운 배경
        ) {
            MemorialProfileContent(item = mockItem)
        }
    }
}