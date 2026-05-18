package com.gentlelady.reborn.feature.memorial.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FollowTheSigns // 예시: 팔로우 아이콘 실제 필요
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
// 컬러 관리 규칙 준수: Semantic 상수를 Import하여 사용[cite: 6, 7].
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDeepBlue // 시안의 primary 버튼 배경 컬러 준수
import com.gentlelady.reborn.core.theme.RebornDividerGray // 시안의 Border 컬러 준수 (또는 White 사용 가능)
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import com.gentlelady.reborn.ic_follow
import com.gentlelady.reborn.ic_send
import com.gentlelady.reborn.ic_external_link

@Composable
internal fun ColumnScope.MemorialBottomButtons(
    memorialId: String,
    onFollowClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onVisitPageClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 상단 팔로우/공유 행
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 팔로우 버튼 (흰색 배경)
            Button(
                onClick = { onFollowClick(memorialId) },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(Res.drawable.ic_follow), contentDescription = null, tint = RebornCobaltBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "팔로우", color = RebornCobaltBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            // 공유하기 버튼 (블루 배경)
            Button(
                onClick = { onShareClick(memorialId) },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RebornDeepBlue), // 시안 블루 컬러 준수
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(Res.drawable.ic_send), contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "공유하기", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // 2. 메모리얼 페이지 보러가기 버튼 (Bordered)
        OutlinedButton(
            onClick = { onVisitPageClick(memorialId) },
            modifier = Modifier.fillMaxWidth().height(50.dp).background(Color.Transparent),
            shape = RoundedCornerShape(12.dp),
            // 시안의 흰색 테두리 준수
            border = BorderStroke(width = 1.dp, color = Color.White.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(Res.drawable.ic_external_link), contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "메모리얼 페이지 보러가기", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
private fun MemorialBottomButtonsPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
        ) {
            MemorialBottomButtons(
                memorialId = "preview_id",
                onFollowClick = {},
                onShareClick = {},
                onVisitPageClick = {}
            )
        }
    }
}