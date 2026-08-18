package com.gentlelady.reborn.feature.management.security.account_visibility.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * "공개 계정" / "비공개 계정"처럼 제목+토글+설명 문구로 구성된 옵션 카드.
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
            .border(1.dp, RebornGridBorderGray, RoundedCornerShape(16.dp))
            .padding(16.dp)
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
                colors = SwitchDefaults.colors(checkedTrackColor = RebornCobaltBlue)
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
            onCheckedChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
