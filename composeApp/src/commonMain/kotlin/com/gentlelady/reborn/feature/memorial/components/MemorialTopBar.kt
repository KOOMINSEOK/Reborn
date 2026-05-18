package com.gentlelady.reborn.feature.memorial.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.feature.memorial.MusicItem
// 컬러 관리 규칙 준수: Semantic 상수를 Import하여 사용[cite: 6, 7].
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornBorderLightBlue
import org.jetbrains.compose.ui.tooling.preview.Preview

// 컴포넌트 분화 및 하위 의존 컴포넌트는 internal로 은닉.
@Composable
internal fun BoxScope.MemorialTopBar(
    musicItem: MusicItem?,
    onHomeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .align(Alignment.TopCenter),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 음악 정보 위젯
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black.copy(alpha = 0.6f)) // 시안에 맞게 Dim 처리 (하드코딩 금지이나 임시)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = Color.LightGray) {
                // dynamic 에셋 플레이스홀더 사용 규칙 준수.
                Text("Art", color = Color.White, modifier = Modifier.wrapContentSize(Alignment.Center))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(musicItem?.title ?: "No Title", color = Color.White, fontSize = 14.sp)
                Text(musicItem?.artist ?: "Unknown Artist", color = Color.LightGray, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                // 에셋 관리 규칙 준수:ic_prefix 및 평평한 drawable 관리[cite: 3, 30].
                // painterResource(Res.drawable.ic_music_note) // 실제 에셋 필요[cite: 43].
                imageVector = Icons.Filled.MusicNote,
                contentDescription = "Music",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        // 2. 홈 버튼
        IconButton(
            onClick = onHomeClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.8f)) // 시안에 맞게 semi-transparent
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// BoxScope 확장성 대응을 위한 단독 프리뷰 래퍼 구현
@Preview
@Composable
private fun MemorialTopBarPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.DarkGray) // 시안의 어두운 배경 시각화용 가상 배경
        ) {
            MemorialTopBar(
                musicItem = MusicItem("See You Again", "Wiz Khalifa ft. Charlie Puth", ""),
                onHomeClick = {}
            )
        }
    }
}