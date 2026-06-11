package com.gentlelady.reborn.core.designsystem.MemorialCard // 대문자 컨벤션 유지

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Place // 조화 마크 대용 (필요시 커스텀 리소스 교체 가능)
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.img_flower
import com.gentlelady.reborn.search.domain.entity.MemorialSearchItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MemorialProfileItem(
    item: MemorialSearchItem,
    onVisitClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 기획서 시안: 개별 항목이 테두리가 있는 독립된 Card 컨테이너 형태로 배치됨
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp), // 카드 간 간격 조정
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RebornWhite),
        border = BorderStroke(1.dp, RebornDividerGray) // 외곽선 국룰 디자인 반영
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp), // 내부 패딩 확보
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. 아바타 프로필 레이어 + 우측 하단 랭킹 배지 겹치기
            Box(modifier = Modifier.size(52.dp)) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = RebornSurfaceVariant
                ) {
                    item.profileImageUrl?.let { url ->
                        Image(
                            painter = painterResource(url),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // 랭킹 배지 (시안: 1~3등은 Cobalt Blue, 4등부터는 Gray 배경)
                Surface(
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.BottomEnd),
                    shape = CircleShape,
                    color = if (item.rank <= 3) RebornCobaltBlue else RebornUnselectedGray
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.BottomEnd)
                            .background(
                                color = if (item.rank <= 3) RebornCobaltBlue else RebornUnselectedGray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.rank.toString(),
                            color = RebornWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            style = LocalTextStyle.current.copy(
                                // includeFontPadding 대신 KMP 공통 스펙인 LineHeightStyle로 정렬 보정
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both
                                )
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // 2. 프로필 정보 영역 (이름, 인증 배지, 생몰 정보)
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
                Spacer(modifier = Modifier.height(4.dp))
                // 시안 포맷: 1965 — 2023 · 서울
                Text(
                    text = "${item.birthDate} — ${item.deathDate} · ${item.location}",
                    color = RebornSlateGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(6.dp))

                // 시안: 조화 마크 아이콘과 개수가 수평(Row)으로 이름 아래 정렬됨
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 💡 PNG 리소스를 안전하게 불러와 시맨틱 컬러(CobaltBlue)로 Tint 처리
                    Icon(
                        painter = painterResource(Res.drawable.img_flower), // png 리소스 바인딩
                        contentDescription = "Flower",
                        tint = RebornCobaltBlue, // 이미지 색상을 테마에 맞게 강제 적용
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.flowerCount,
                        color = RebornCobaltBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 3. 우측 방문 버튼 레이어
            OutlinedButton(
                onClick = { onVisitClick(item.id) },
                shape = RoundedCornerShape(8.dp), // 시안에 맞춰 약간 각진 라운드스퀘어 형태로 조율
                border = BorderStroke(1.dp, RebornBorderLightBlue),
                modifier = Modifier.height(34.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RebornCobaltBlue)
            ) {
                Text("방문", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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