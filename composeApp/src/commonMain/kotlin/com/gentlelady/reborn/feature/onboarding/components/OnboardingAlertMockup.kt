package com.gentlelady.reborn.feature.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornSoftBlue
import com.gentlelady.reborn.core.theme.RebornTextPrimary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun OnboardingAlertMockup() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RebornLightBlueBg, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = RebornDeepBlue,
                modifier = Modifier.size(20.dp)
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Row {
                    Text(text = "RE:BORN 알림", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = RebornTextPrimary)
                    Text(text = "  지금", fontSize = 11.sp, color = RebornSlateGray)
                }
                Text(
                    text = "오늘은 故 김철수 님의 기일입니다.\n따뜻한 인사를 남겨보세요.",
                    fontSize = 12.sp,
                    color = RebornTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.padding(top = 12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(RebornSoftBlue, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(RebornDeepBlue, CircleShape)
            )
            Text(
                text = "시들지 않는 마음의 꽃",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = RebornTextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "마음을 가득 담은 푸른 국화와 위로의 메시지",
                fontSize = 11.sp,
                color = RebornSlateGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun OnboardingAlertMockupPreview() {
    MaterialTheme {
        OnboardingAlertMockup()
    }
}
