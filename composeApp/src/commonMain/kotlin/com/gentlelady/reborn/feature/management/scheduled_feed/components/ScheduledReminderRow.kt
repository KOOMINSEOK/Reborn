package com.gentlelady.reborn.feature.management.scheduled_feed.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.CircleAvatarImage
import com.gentlelady.reborn.core.designsystem.components.OverflowAction
import com.gentlelady.reborn.core.designsystem.components.OverflowActionMenu
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.img_memorial_profile_dummy
import com.gentlelady.reborn.management.scheduled_feed.domain.model.ScheduledReminderItem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ScheduledReminderRow(
    item: ScheduledReminderItem,
    actions: List<OverflowAction>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, RebornGridBorderGray, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        CircleAvatarImage(
            imageRes = item.recipientAvatar,
            size = 55.dp,
            fallbackText = item.recipientName,
            borderWidth = 0.dp,
            shadowElevation = 0.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.recipientName,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                OverflowActionMenu(actions = actions)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.scheduleLabel,
                color = RebornCobaltBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.messagePreview,
                color = RebornSlateGray,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
private fun ScheduledReminderRowPreview() {
    MaterialTheme {
        ScheduledReminderRow(
            item = ScheduledReminderItem(
                id = "1",
                recipientName = "박희옥",
                scheduleLabel = "사망 후 3일 뒤 예약됨",
                messagePreview = "늘 감사하고 사랑합니다...",
                recipientAvatar = Res.drawable.img_memorial_profile_dummy
            ),
            actions = listOf(
                OverflowAction("수정") {},
                OverflowAction("삭제", isDestructive = true) {},
                OverflowAction("지금 보내기") {}
            )
        )
    }
}
