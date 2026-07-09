package com.gentlelady.reborn.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ProfileManagementGrid(
    scheduledCount: Int,
    onMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "관리",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 2열 그리드 구조 분화 (Row-Column 가중치 매칭으로 고정 크기 보장)
        Row(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ManagementCardItem(
                title = "예약된\n피드 관리",
                icon = Icons.Default.DateRange,
                badgeCount = scheduledCount,
                isHighlighted = true, // 코발트 블루 강조 카드 지정
                onClick = { onMenuClick("scheduled_feed") }
            )
            ManagementCardItem(
                title = "프로필\n편집",
                icon = Icons.Default.Edit,
                badgeCount = 0,
                isHighlighted = false,
                onClick = { onMenuClick("edit_profile") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ManagementCardItem(
                title = "보관",
                icon = Icons.Default.Build, // 임시 매칭 에셋 대체 가능
                badgeCount = 0,
                isHighlighted = false,
                onClick = { onMenuClick("archive") }
            )
            ManagementCardItem(
                title = "사후 관리인\n지정",
                icon = Icons.Default.Favorite, // 도메인 특화 하트 아이콘 계열 매칭
                badgeCount = 0,
                isHighlighted = false,
                onClick = { onMenuClick("posthumous_manager") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ManagementCardItem(
                title = "공개 범위 및\n보안",
                icon = Icons.Default.Lock,
                badgeCount = 0,
                isHighlighted = false,
                onClick = { onMenuClick("security") }
            )
            ManagementCardItem(
                title = "앱 설정 및\n고객센터",
                icon = Icons.Default.Settings,
                badgeCount = 0,
                isHighlighted = false,
                onClick = { onMenuClick("settings") }
            )
        }
    }
}

@Composable
private fun RowScope.ManagementCardItem(
    title: String,
    icon: ImageVector,
    badgeCount: Int,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 하이라이트 여부에 따른 외곽선 및 배경색 세맨틱 할당 규칙 준수
    val borderStroke = if (isHighlighted) {
        Modifier.border(1.5.dp, RebornCobaltBlue, RoundedCornerShape(16.dp))
    } else {
        Modifier.border(1.dp, RebornDividerGray, RoundedCornerShape(16.dp))
    }

    val containerColor = if (isHighlighted) RebornLightBlueBg else RebornBackgroundGray

    Box(
        modifier = modifier
            .weight(1f)
            .fillMaxHeight()
            .then(borderStroke)
            .background(containerColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            // 상단 아이콘 박스
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isHighlighted) RebornCobaltBlue else RebornSlateGray,
                    modifier = Modifier.size(20.dp)
                )
            }

            // 하단 메뉴 텍스트 (시안 특유의 행간 및 볼드 묘사)
            Text(
                text = title,
                color = if (isHighlighted) RebornDeepBlue else Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp
            )
        }

        // 우상단 블루 카운트 배지 (신호 숫자 "3" 묘사 규칙)
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(RebornDeepBlue, shape = CircleShape)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- 프리뷰 규칙 준수: PreviewParameterProvider 차단 및 Direct Injection 데이터 적용 ---
@Preview
@Composable
private fun ProfileManagementGridPreview() {
    MaterialTheme {
        ProfileManagementGrid(
            scheduledCount = 3,
            onMenuClick = {}
        )
    }
}