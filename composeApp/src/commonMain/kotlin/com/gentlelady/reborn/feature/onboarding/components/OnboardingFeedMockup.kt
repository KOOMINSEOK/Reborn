package com.gentlelady.reborn.feature.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.RebornCard
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornBackground
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornTextPrimary
import com.gentlelady.reborn.ic_thumbs_up
import com.gentlelady.reborn.img_onboarding_post
import com.gentlelady.reborn.img_onboarding_profile_2
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun OnboardingFeedMockup() {
    // 게시물 이미지를 카드 좌우 끝까지 채우기 위해 카드 자체 패딩은 끄고,
    // 이미지를 제외한 나머지 요소에 개별적으로 좌우 패딩(16.dp)을 준다.
    RebornCard(contentPadding = 0.dp) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.img_onboarding_profile_2),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(text = "이영희", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RebornTextPrimary)
                Text(text = "함께 추억하는 공간", fontSize = 12.sp, color = RebornSlateGray)
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().height(180.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.img_onboarding_post),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(180.dp)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .background(RebornDeepBlue, RoundedCornerShape(15.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(text = "\"가장 환하게 웃던 날\"", fontSize = 12.sp, color = Color.White)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(Res.drawable.ic_thumbs_up),
                    contentDescription = null,
                    tint = RebornDeepBlue,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "서로 격려해요 (48)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RebornDeepBlue,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
            Text(text = "댓글 12개", fontSize = 12.sp, color = RebornSlateGray)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .background(RebornBackground, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "박지성", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RebornDeepBlue)
                    Box(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .background(RebornLightBlueBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "가족", fontSize = 10.sp, color = RebornDeepBlue)
                    }
                }
                Text(
                    text = "\"우리 다 같이 힘내서 잘 살자!\"",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RebornTextPrimary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
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
