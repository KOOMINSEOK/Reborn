package com.gentlelady.reborn.core.designsystem.Memorialcard // 1. 패키지명 소문자 교정

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.* // 2. 정리된 시맨틱 테마 컬러 일괄 참조
import com.gentlelady.reborn.search.domain.entity.MemorialSearchItem // 3. 검색 리스트 전용 모델 임포트
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MemorialProfileItem(
    item: MemorialSearchItem, // 4. SearchContract와 타입을 일치시켜 에러 원천 차단
    onVisitClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 아바타 프로필 레이어 + 우측 하단 랭킹 배지 (기획서 시안 반영)
        Box(modifier = Modifier.size(48.dp)) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = RebornSurfaceVariant
            ) {
                if (item.profileImageUrl != null) {
                    Image(
                        painter = painterResource(item.profileImageUrl!!),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // 랭킹 배지 (오른쪽 아래 겹치기 배치)
            Surface(
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.BottomEnd),
                shape = CircleShape,
                color = if (item.rank <= 3) RebornCobaltBlue else RebornUnselectedGray
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = item.rank.toString(),
                        color = RebornWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 2. 프로필 정보 (이름 앞 '故' 접두사 고정 및 인증 배지 렌더링)
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "故 ${item.name}",
                    color = RebornTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                if (item.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = RebornCobaltBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            // 기획서 시안 규격 포맷팅: 생몰연도 · 지역
            Text(
                text = "${item.birthDate} — ${item.deathDate} · ${item.location}",
                color = RebornSlateGray,
                fontSize = 12.sp
            )
        }

        // 3. 꽃 개수 및 방문 버튼
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = item.flowerCount,
                color = RebornCobaltBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = { onVisitClick(item.id) },
                shape = RoundedCornerShape(9999.dp),
                border = BorderStroke(1.dp, RebornBorderLightBlue),
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RebornCobaltBlue)
            ) {
                Text("방문", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ========================================================================
// KMP Strict Rules 준수형 단독 프리뷰 (Direct Injection)
// ========================================================================

@Preview
@Composable
private fun MemorialProfileItemTopRankPreview() {
    val mockItem = MemorialSearchItem(
        id = "1",
        rank = 1,
        name = "이수진",
        birthDate = "1965",
        deathDate = "2023",
        location = "서울",
        flowerCount = "24.8k",
        profileImageUrl = null,
        isVerified = false
    )
    MaterialTheme {
        Surface(color = RebornBackground) {
            MemorialProfileItem(item = mockItem, onVisitClick = {})
        }
    }
}

@Preview
@Composable
private fun MemorialProfileItemNormalRankPreview() {
    val mockItem = MemorialSearchItem(
        id = "4",
        rank = 4,
        name = "최동현",
        birthDate = "1955",
        deathDate = "2021",
        location = "대구",
        flowerCount = "9.7k",
        profileImageUrl = null,
        isVerified = false
    )
    MaterialTheme {
        Surface(color = RebornBackground) {
            MemorialProfileItem(item = mockItem, onVisitClick = {})
        }
    }
}