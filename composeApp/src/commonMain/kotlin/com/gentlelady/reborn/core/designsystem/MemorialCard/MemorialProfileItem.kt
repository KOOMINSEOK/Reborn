package com.gentlelady.reborn.core.designsystem.MemorialCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.img_memorial_bg_dummy
import com.gentlelady.reborn.img_memorial_profile_dummy
import com.gentlelady.reborn.memorial_swipe.domain.model.MemorialItem // 모델 임포트
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MemorialProfileItem(
    item: MemorialItem, // 모델 자체를 전달
    onVisitClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 랭킹 배지 (item.rank 사용)
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = if (item.rank <= 3) RebornCobaltBlue else Color.Transparent
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = item.rank.toString(),
                    color = if (item.rank <= 3) Color.White else RebornUnselectedGray,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 2. 프로필 정보 (item 필드 매핑)
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("${item.jobTitle} | ${item.location}", color = RebornSlateGray, fontSize = 12.sp)
        }

        // 3. 꽃 개수 및 방문 버튼
        Column(horizontalAlignment = Alignment.End) {
            Text(item.flowerCount, color = RebornCobaltBlue, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = { onVisitClick(item.id) },
                shape = RoundedCornerShape(9999.dp),
                border = BorderStroke(1.dp, RebornBorderLightBlue),
                modifier = Modifier.height(32.dp)
            ) {
                Text("방문", color = RebornCobaltBlue, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true, name = "Memorial Profile Item - Top Rank")
@Composable
private fun MemorialProfileItemTopRankPreview() {
    val mockItem = MemorialItem(
        id = "1",
        rank = 1,
        name = "김첨지",
        jobTitle = "소방관",
        location = "서울특별시",
        flowerCount = "24.8k",
        birthDate = "1987.03.02",
        deathDate = "2024.01.03",
        profileImageUrl = com.gentlelady.reborn.Res.drawable.img_memorial_profile_dummy,
        backgroundImageUrl = com.gentlelady.reborn.Res.drawable.img_memorial_bg_dummy,
        currentMusic = null
    )
    MaterialTheme {
        Surface {
            MemorialProfileItem(item = mockItem, onVisitClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Memorial Profile Item - Normal Rank")
@Composable
private fun MemorialProfileItemNormalRankPreview() {
    val mockItem = MemorialItem(
        id = "4",
        rank = 4,
        name = "이순신",
        jobTitle = "장군",
        location = "충청남도",
        flowerCount = "1.2k",
        birthDate = "1545.04.28",
        deathDate = "1598.12.16",
        profileImageUrl = com.gentlelady.reborn.Res.drawable.img_memorial_profile_dummy,
        backgroundImageUrl = com.gentlelady.reborn.Res.drawable.img_memorial_bg_dummy,
        currentMusic = null
    )
    MaterialTheme {
        Surface {
            MemorialProfileItem(item = mockItem, onVisitClick = {})
        }
    }
}