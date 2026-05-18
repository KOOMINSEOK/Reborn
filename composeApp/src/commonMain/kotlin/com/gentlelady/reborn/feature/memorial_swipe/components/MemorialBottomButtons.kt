package com.gentlelady.reborn.feature.memorial_swipe.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
// 컬러 관리 규칙 준수: 프로젝트 테마 세맨틱 상수 사용
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

// KMP 에셋 관리 규칙에 따른 정적 리소스 정상 임포트
import com.gentlelady.reborn.ic_follow
import com.gentlelady.reborn.ic_send
import com.gentlelady.reborn.ic_external_link

@Composable
internal fun ColumnScope.MemorialBottomButtons(
    memorialId: String,
    onFollowClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onVisitPageClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 상단 주 액션 레이아웃 (팔로우 / 공유하기 행)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 팔로우 버튼 (반전 가독성을 위한 화이트 배경)
            Button(
                onClick = { onFollowClick(memorialId) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_follow),
                        contentDescription = null,
                        tint = RebornCobaltBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "팔로우",
                        color = RebornCobaltBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 공유하기 버튼 (주요 브랜드 컬러 매칭)
            Button(
                onClick = { onShareClick(memorialId) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RebornDeepBlue),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_send),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "공유하기",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // 2. [특성 최적화] "Go to Memorial Page" full-width ghost button below primary actions
        // 전체 화면 몰입형(Fully Immersive) 테마의 투명 감성을 위해 무거운 background 속성을 걷어내고,
        // 시안의 선명한 반투명 테두리선을 결합한 순수 고스트(Ghost Bordered) 버튼으로 완성했습니다.
        OutlinedButton(
            onClick = { onVisitPageClick(memorialId) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(width = 1.dp, color = Color.White.copy(alpha = 0.4f)), // 은은한 플로팅 경계선 처리
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent, // 완전한 고스트 버튼 투명도 확보
                contentColor = Color.White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_external_link),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "메모리얼 페이지 보러가기",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ColumnScope 명시적 대응 및 단독 렌더링 확인을 위한 프리뷰 구조 (지역 함수 선언 금지 원칙 준수)
@Preview
@Composable
private fun MemorialBottomButtonsPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray) // 몰입형 어두운 배경 시각화 레이어
        ) {
            MemorialBottomButtons(
                memorialId = "preview_id",
                onFollowClick = {},
                onShareClick = {},
                onVisitPageClick = {}
            )
        }
    }
}