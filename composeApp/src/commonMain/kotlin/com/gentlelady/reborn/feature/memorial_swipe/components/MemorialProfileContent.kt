package com.gentlelady.reborn.feature.memorial_swipe.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornBorderLightBlue
import com.gentlelady.reborn.ic_memorial_ribbon
import com.gentlelady.reborn.img_memorial_profile_dummy
import com.gentlelady.reborn.memorial_swipe.domain.model.MemorialItem

@Composable
internal fun ColumnScope.MemorialProfileContent(item: MemorialItem) {
    val infiniteTransition = rememberInfiniteTransition(label = "RibbonGlowTransition")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowScaleValue"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlphaValue"
    )

    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(64.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(glowScale)
                    .alpha(glowAlpha)
                    .background(RebornDeepBlue, shape = CircleShape)
            )

            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .border(width = 2.dp, color = RebornBorderLightBlue, shape = CircleShape),
                shape = CircleShape,
                color = RebornDeepBlue
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_memorial_ribbon),
                        contentDescription = "Ribbon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp).padding(2.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.Black.copy(alpha = 0.5f),
            border = BorderStroke(
                width = 1.dp,
                color = RebornBorderLightBlue.copy(alpha = 0.4f)
            ),
            modifier = Modifier.wrapContentSize()
        ) {
            Text(
                text = "${item.birthDate} — ${item.deathDate}",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .border(width = 4.dp, color = Color.White, shape = CircleShape)
        ) {
            if (item.profileImageUrl.isEmpty()) {
                Image(
                    painter = painterResource(Res.drawable.img_memorial_profile_dummy),
                    contentDescription = "Memorial Profile Dummy Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // 추후 연동용 분기 개설 완료
                Image(
                    painter = painterResource(Res.drawable.img_memorial_profile_dummy),
                    contentDescription = "Memorial Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = item.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "${item.jobTitle} , ${item.location}", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
    }
}

@Preview
@Composable
private fun MemorialProfileContentPreview() {
    val mockItem = MemorialItem(
        id = "1",
        name = "김첨지",
        jobTitle = "소방관",
        location = "서울특별시",
        birthDate = "1987.03.02",
        deathDate = "2024.01.03",
        profileImageUrl = "",
        backgroundImageUrl = "",
        currentMusic = null
    )
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .background(Color.DarkGray)
        ) {
            MemorialProfileContent(item = mockItem)
        }
    }
}