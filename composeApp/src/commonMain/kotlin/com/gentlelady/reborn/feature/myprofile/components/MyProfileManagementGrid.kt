package com.gentlelady.reborn.feature.myprofile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.ic_archive
import com.gentlelady.reborn.ic_bookmark
import com.gentlelady.reborn.ic_profile_edit
import com.gentlelady.reborn.ic_schedule
import com.gentlelady.reborn.ic_settings_tune
import com.gentlelady.reborn.ic_shield_check
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MyProfileManagementGrid(
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

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ManagementRowItem(
                title = "예약된 피드 및 리마인드 관리",
                icon = Res.drawable.ic_schedule,
                badgeCount = scheduledCount,
                isHighlighted = true,
                onClick = { onMenuClick("scheduled_feed") }
            )
            ManagementRowItem(
                title = "프로필 및 결제 관리",
                icon = Res.drawable.ic_profile_edit,
                badgeCount = 0,
                isHighlighted = false,
                onClick = { onMenuClick("edit_profile") }
            )
            ManagementRowItem(
                title = "보관",
                icon = Res.drawable.ic_archive,
                badgeCount = 0,
                isHighlighted = false,
                onClick = { onMenuClick("archive") }
            )
            ManagementRowItem(
                title = "저장(스크랩)",
                icon = Res.drawable.ic_bookmark,
                badgeCount = 0,
                isHighlighted = false,
                onClick = { onMenuClick("saved") }
            )
            ManagementRowItem(
                title = "공개 범위 및 보안",
                icon = Res.drawable.ic_shield_check,
                badgeCount = 0,
                isHighlighted = false,
                onClick = { onMenuClick("security") }
            )
            ManagementRowItem(
                title = "앱 설정 및 고객센터",
                icon = Res.drawable.ic_settings_tune,
                badgeCount = 0,
                isHighlighted = false,
                onClick = { onMenuClick("settings") }
            )
        }
    }
}

@Composable
private fun ManagementRowItem(
    title: String,
    icon: DrawableResource,
    badgeCount: Int,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBackground = if (isHighlighted) RebornGridCardBgHighlight else RebornGridCardBgNormal
    val cardBorderColor = if (isHighlighted) RebornGridCardBorderHighlight else RebornGridCardBorderNormal
    val iconBoxBackground = if (isHighlighted) RebornGridCardIconBgHighlight else RebornGridCardIconBgNormal
    val iconTintColor = if (isHighlighted) RebornGridCardIconTintHighlight else RebornSlateGray

    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = cardBorderColor, shape = RoundedCornerShape(16.dp))
            .background(color = cardBackground, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(color = iconBoxBackground, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = title,
                tint = iconTintColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            color = if (isHighlighted) RebornPrimary else Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(RebornDeepBlue, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = RebornSlateGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// --- 프리뷰 규칙 준수: PreviewParameterProvider 차단 및 Direct Injection 데이터 적용 ---
@Preview
@Composable
private fun MyProfileManagementGridPreview() {
    MaterialTheme {
        MyProfileManagementGrid(
            scheduledCount = 3,
            onMenuClick = {}
        )
    }
}
