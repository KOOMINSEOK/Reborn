package com.gentlelady.reborn.feature.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
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
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornSurfaceVariant
import com.gentlelady.reborn.core.theme.RebornTextPrimary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun OnboardingFeedMockup() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(32.dp).background(RebornSurfaceVariant, CircleShape))
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = "이영희", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = RebornTextPrimary)
                Text(text = "함께 추억하는 공간", fontSize = 11.sp, color = RebornSlateGray)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(top = 10.dp)
                .background(RebornDeepBlue, RoundedCornerShape(12.dp))
        ) {
            Text(
                text = "\"가장 힘하게 뭉친 밤\"",
                fontSize = 11.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size(14.dp))
            Text(text = "서로 격려해요 (48)", fontSize = 11.sp, color = RebornSlateGray, modifier = Modifier.padding(start = 4.dp))
            Spacer(modifier = Modifier.padding(start = 10.dp))
            Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size(14.dp))
            Text(text = "댓글 12", fontSize = 11.sp, color = RebornSlateGray, modifier = Modifier.padding(start = 4.dp))
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
            Text(text = "박지연  ", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = RebornTextPrimary)
            Text(text = "\"우리 다 같이 힘내서 잘 살자!\"", fontSize = 11.sp, color = RebornSlateGray)
        }
    }
}

@Preview
@Composable
private fun OnboardingFeedMockupPreview() {
    MaterialTheme {
        OnboardingFeedMockup()
    }
}
