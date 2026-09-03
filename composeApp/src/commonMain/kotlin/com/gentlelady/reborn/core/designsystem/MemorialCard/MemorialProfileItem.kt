package com.gentlelady.reborn.core.designsystem.MemorialCard // 대문자 컨벤션 유지

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.img_flower
import com.gentlelady.reborn.search.domain.entity.MemorialSearchItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 검색 결과 행: 아바타 + 이름 + 조화 수 + 우측 액션 버튼(프로필/메모리얼).
 * 버튼 노출은 item.hasProfile / item.hasMemorial 로 제어된다.
 */
@Composable
internal fun MemorialProfileItem(
    item: MemorialSearchItem,
    onVisitClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RebornWhite),
        border = BorderStroke(1.dp, RebornDividerGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = RebornSurfaceVariant
            ) {
                item.profileImageUrl?.let { url ->
                    Image(
                        painter = painterResource(url),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (item.isDeceased) "故 ${item.name}" else item.name,
                        color = RebornTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (item.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = RebornCobaltBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(Res.drawable.img_flower),
                        contentDescription = "Flower",
                        tint = RebornCobaltBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.flowerCount,
                        color = RebornCobaltBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (item.hasProfile) {
                    OutlinedButton(
                        onClick = { onVisitClick(item.id) },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, RebornBorderLightBlue),
                        modifier = Modifier.height(34.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RebornCobaltBlue)
                    ) {
                        Text("프로필", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                if (item.hasMemorial) {
                    Button(
                        onClick = { onVisitClick(item.id) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RebornCobaltBlue)
                    ) {
                        Text("메모리얼", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MemorialProfileItemPreview() {
    MaterialTheme {
        Surface(color = RebornBackground) {
            Column {
                MemorialProfileItem(
                    item = MemorialSearchItem(
                        id = "1", rank = 1, name = "이수진", birthDate = "1965", deathDate = "2023",
                        location = "서울", flowerCount = "24.8k", profileImageUrl = null,
                        isDeceased = false, hasProfile = true, hasMemorial = true
                    ),
                    onVisitClick = {}
                )
                MemorialProfileItem(
                    item = MemorialSearchItem(
                        id = "3", rank = 3, name = "이수진", birthDate = "1951", deathDate = "2019",
                        location = "인천", flowerCount = "8.6k", profileImageUrl = null,
                        isDeceased = true, hasProfile = false, hasMemorial = true
                    ),
                    onVisitClick = {}
                )
            }
        }
    }
}
