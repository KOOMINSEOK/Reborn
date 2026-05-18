package com.gentlelady.reborn.feature.memorial.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
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
import com.gentlelady.reborn.feature.memorial.MemorialItem
import com.gentlelady.reborn.core.theme.RebornCobaltBlue // 시안 포인트 컬러로 사용[cite: 6].
import org.jetbrains.compose.resources.painterResource

import com.gentlelady.reborn.ic_memorial_ribbon
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ColumnScope.MemorialProfileContent(item: MemorialItem) {
    Column(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. 리본 배지
        Surface(
            modifier = Modifier.size(48.dp).padding(4.dp),
            shape = CircleShape,
            color = RebornCobaltBlue // 시안의 블루 배지 배경 컬러 준수 [cite: 11, 51]
        ) {
            Icon(
                // painterResource와 Res 클래스를 활용해 xml 에셋을 정상적으로 로드합니다. [cite: 17, 51]
                painter = painterResource(Res.drawable.ic_memorial_ribbon),
                contentDescription = "Ribbon",
                tint = Color.White,
                modifier = Modifier.size(24.dp).padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 2. 날짜 태그
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${item.birthDate} — ${item.deathDate}",
                color = Color.White,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // 3. 프로필 이미지 (테두리 하이라이트)
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .border(width = 4.dp, color = Color.White, shape = CircleShape) // 시안의 흰색 테두리 준수
        ) {
            // 동적 에셋 플레이스홀더 사용 규칙 준수.
            Surface(modifier = Modifier.fillMaxSize(), shape = CircleShape, color = Color.LightGray) {
                Text("Profile", color = Color.White, modifier = Modifier.wrapContentSize(Alignment.Center))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // 4. 이름 및 정보 텍스트
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
            // ColumnScope 확장 함수 호출을 위한 구조 형성
            MemorialProfileContent(item = mockItem)
        }
    }
}