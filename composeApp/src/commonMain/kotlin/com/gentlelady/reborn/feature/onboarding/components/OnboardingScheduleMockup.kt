package com.gentlelady.reborn.feature.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.gentlelady.reborn.core.theme.RebornBackground
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornTextPrimary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun OnboardingScheduleMockup() {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "사후 피드 예약 관리", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = RebornTextPrimary)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = {}) {
                Text(text = "기록 보관소", fontSize = 11.sp, color = RebornDeepBlue)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .background(RebornBackground, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "📝 나의 가장 빛나는 순간 기록하기",
                fontSize = 12.sp,
                color = RebornTextPrimary
            )
            Text(
                text = "\"사랑하는 가족들에게. 내가 세상을\n떠나더라도 매년 내 생일에는 슬퍼하\n기보다 다 함께 모여 맛있는 음식을 먹\n으며 웃어주길 바란다...\"",
                fontSize = 11.sp,
                color = RebornSlateGray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "지정일 자동 발행", fontSize = 12.sp, color = RebornTextPrimary)
            Spacer(modifier = Modifier.weight(1f))
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
