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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.feature.memorial_swipe.MusicItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import com.gentlelady.reborn.core.theme.RebornBorderLightBlue
import com.gentlelady.reborn.ic_music
import com.gentlelady.reborn.ic_nav_home_default
import com.gentlelady.reborn.img_memorial_album_dummy

@Composable
internal fun BoxScope.MemorialTopBar(
    musicItem: MusicItem?,
    onHomeClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "MusicDiscRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DiscRotationAngle"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .align(Alignment.TopCenter),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .border(
                    border = BorderStroke(1.dp, RebornBorderLightBlue.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .rotate(rotation)
                    .border(
                        width = 1.dp,
                        color = RebornBorderLightBlue.copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                shape = CircleShape,
                color = Color.DarkGray
            ) {
                // 외부 데이터의 가용성에 따른 이미지 바인딩 최적화 (Stateless 분기)
                if (musicItem?.albumImageUrl.isNullOrEmpty()) {
                    Image(
                        painter = painterResource(Res.drawable.img_memorial_album_dummy),
                        contentDescription = "Default Album Art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // 추후 확장: AsyncImage 로더(Coil 등) 대응 영역
                    Image(
                        painter = painterResource(Res.drawable.img_memorial_album_dummy),
                        contentDescription = "Dynamic Album Art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Text(
                    text = musicItem?.title ?: "No Title",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = musicItem?.artist ?: "Unknown Artist",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                painter = painterResource(Res.drawable.ic_music),
                contentDescription = "Music",
                tint = Color.White,
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        IconButton(
            onClick = onHomeClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .border(
                    width = 1.dp,
                    color = RebornBorderLightBlue.copy(alpha = 0.4f),
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_nav_home_default),
                contentDescription = "Home",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MemorialTopBarPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.DarkGray)
        ) {
            MemorialTopBar(
                musicItem = MusicItem("See You Again", "Wiz Khalifa ft. Charlie Puth", ""),
                onHomeClick = {}
            )
        }
    }
}