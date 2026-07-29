package com.gentlelady.reborn.feature.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.NotificationCard
import com.gentlelady.reborn.core.designsystem.components.RebornCard
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornTextPrimary
import com.gentlelady.reborn.ic_alarm
import com.gentlelady.reborn.ic_flower
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun OnboardingAlertMockup() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NotificationCard(
            icon = Res.drawable.ic_alarm,
            title = "RE:BORN 알림",
            timestamp = "지금",
            message = "오늘은 故 김철수 님의 기일입니다.\n따뜻한 인사를 남겨보세요."
        )

        RebornCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(72.dp).background(RebornLightBlueBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_flower),
                        contentDescription = null,
                        tint = RebornDeepBlue,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .background(RebornDeepBlue, RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "온라인 화환", fontSize = 11.sp, color = Color.White)
                }

                Text(
                    text = "시들지 않는 마음의 꽃",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = RebornTextPrimary,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = "마음을 가득 담은 푸른 국화와 위로의 메시지",
                    fontSize = 12.sp,
                    color = RebornSlateGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
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
