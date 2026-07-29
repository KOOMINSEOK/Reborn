package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornTextPrimary
import com.gentlelady.reborn.ic_alarm
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 온보딩뿐 아니라 다른 화면의 알림성 카드에도 재사용하는 공용 컴포넌트.
 */
@Composable
fun NotificationCard(
    icon: DrawableResource,
    title: String,
    timestamp: String,
    message: String,
    modifier: Modifier = Modifier
) {
    RebornCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(40.dp).background(RebornLightBlueBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = RebornDeepBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.padding(start = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RebornDeepBlue)
                    Text(text = timestamp, fontSize = 12.sp, color = RebornSlateGray)
                }
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = RebornTextPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun NotificationCardPreview() {
    MaterialTheme {
        NotificationCard(
            icon = Res.drawable.ic_alarm,
            title = "RE:BORN 알림",
            timestamp = "지금",
            message = "오늘은 故 김철수 님의 기일입니다.\n따뜻한 인사를 남겨보세요."
        )
    }
}
