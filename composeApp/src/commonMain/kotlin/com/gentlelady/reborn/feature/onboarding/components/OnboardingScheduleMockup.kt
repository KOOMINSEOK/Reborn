package com.gentlelady.reborn.feature.onboarding.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.RebornCard
import com.gentlelady.reborn.core.theme.RebornBackground
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornTextPrimary
import com.gentlelady.reborn.ic_calendar
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun OnboardingScheduleMockup() {
    RebornCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(8.dp).background(RebornDeepBlue, CircleShape)
            )
            Text(
                text = "사후 피드 예약 관리",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = RebornTextPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .background(RebornLightBlueBg, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = "기록 보관소", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = RebornDeepBlue)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
                .background(RebornDividerGray)
                .height(1.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
                .background(RebornBackground, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "✏️ 나의 가장 빛나는 순간 기록하기",
                fontSize = 13.sp,
                color = RebornSlateGray
            )
            Text(
                text = "\"사랑하는 가족들에게. 내가 세상을\n떠나더라도 매년 내 생일에는 슬퍼하\n기보다 다 함께 모여 맛있는 음식을 먹\n으며 웃어주길 바란다...\"",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = RebornTextPrimary,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .border(BorderStroke(1.dp, RebornGridBorderGray), RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(RebornLightBlueBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_calendar),
                    contentDescription = null,
                    tint = RebornDeepBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
                Text(text = "지정일 자동 발행", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RebornTextPrimary)
                Text(text = "매년 나의 기일 또는 생일에 맞춤 공개", fontSize = 12.sp, color = RebornSlateGray)
            }

            var checked by remember { mutableStateOf(true) }
            Switch(
                checked = checked,
                onCheckedChange = { checked = it },
                colors = SwitchDefaults.colors(checkedTrackColor = RebornDeepBlue)
            )
        }
    }
}

@Preview
@Composable
private fun OnboardingScheduleMockupPreview() {
    MaterialTheme {
        OnboardingScheduleMockup()
    }
}
