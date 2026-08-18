package com.gentlelady.reborn.feature.management.saved.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.CircleAvatarImage
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.img_memorial_profile_dummy
import com.gentlelady.reborn.management.saved.domain.model.SavedGuestBookItem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun SavedGuestBookRow(
    item: SavedGuestBookItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        CircleAvatarImage(
            imageRes = item.authorAvatar,
            size = 48.dp,
            fallbackText = item.memorialName,
            borderWidth = 0.dp,
            shadowElevation = 0.dp
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.memorialName,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.dateLabel,
                    color = RebornSlateGray,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.message,
                color = Color.Black,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Preview
@Composable
private fun SavedGuestBookRowPreview() {
    MaterialTheme {
        SavedGuestBookRow(
            item = SavedGuestBookItem(
                id = "1",
                memorialName = "故 김영희 님의 메모리얼",
                message = "하늘에서도 평안하시길 바랍니다. 늘 기억하겠습니다.",
                dateLabel = "2026.07.27 18:27",
                authorAvatar = Res.drawable.img_memorial_profile_dummy
            ),
            onClick = {}
        )
    }
}
