package com.gentlelady.reborn.feature.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornSurfaceVariant
import com.gentlelady.reborn.core.theme.RebornTextPrimary
import com.gentlelady.reborn.core.theme.RebornWhite
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun OnboardingMemorialCardMockup() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).background(RebornSurfaceVariant, CircleShape)
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = "김철수 님을 기억하며", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = RebornTextPrimary)
                Text(text = "2024.06.20 개설", fontSize = 11.sp, color = RebornSlateGray)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .background(RebornWhite)
        ) {
            Text(
                text = "\"그곳에선 아프지 말고 편히 쉬세요.\n당신의 웃음소리가 벌써 그립습니다.\"",
                fontSize = 13.sp,
                color = RebornTextPrimary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "방명록 한 줄", fontSize = 11.sp, color = RebornSlateGray)
            Text(text = "2시간 전", fontSize = 11.sp, color = RebornSlateGray)
        }

        Spacer(modifier = Modifier.padding(top = 6.dp))

        Row(
            modifier = Modifier
                .padding(top = 6.dp)
                .background(RebornDividerGray, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null, tint = RebornDeepBlue, modifier = Modifier.size(14.dp))
            Text(
                text = "12K명이 위로를 전했습니다",
                fontSize = 11.sp,
                color = RebornDeepBlue,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun OnboardingMemorialCardMockupPreview() {
    MaterialTheme {
        OnboardingMemorialCardMockup()
    }
}
