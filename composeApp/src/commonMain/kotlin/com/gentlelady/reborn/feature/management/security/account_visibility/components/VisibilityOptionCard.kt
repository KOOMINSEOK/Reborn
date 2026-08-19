package com.gentlelady.reborn.feature.management.security.account_visibility.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornSwitchTrackOff
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * "공개 계정" / "비공개 계정"처럼 제목+토글+설명 문구로 구성된 옵션 섹션.
 * 카드형이 아니라 좌우 20dp 여백만 두고 구분선으로 나뉘는 형태.
 */
@Composable
internal fun VisibilityOptionCard(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                // 💡 thumbContent를 항상 채워야 unselected 상태에서도 원 크기가 selected와 동일해진다.
                thumbContent = { Box(modifier = Modifier.size(SwitchDefaults.IconSize)) },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = RebornCobaltBlue,
                    uncheckedTrackColor = RebornSwitchTrackOff,
                    uncheckedThumbColor = Color.White,
                    uncheckedBorderColor = RebornSwitchTrackOff
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            color = RebornSlateGray,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )
    }
}

@Preview
@Composable
private fun VisibilityOptionCardPreview() {
    MaterialTheme {
        VisibilityOptionCard(
            title = "공개 계정",
            description = "계정이 공개 상태인 경우 RE:BORN 계정이 없는 사람을 포함해 RE:BORN 안에서 모든 사람이 프로필과 게시물을 볼 수 있습니다.",
            isChecked = true,
            onCheckedChange = {}
        )
    }
}

@Preview
@Composable
private fun VisibilityOptionCardPreview2() {
    MaterialTheme {
        VisibilityOptionCard(
            title = "공개 계정",
            description = "계정이 공개 상태인 경우 RE:BORN 계정이 없는 사람을 포함해 RE:BORN 안에서 모든 사람이 프로필과 게시물을 볼 수 있습니다.",
            isChecked = false,
            onCheckedChange = {}
        )
    }
}

