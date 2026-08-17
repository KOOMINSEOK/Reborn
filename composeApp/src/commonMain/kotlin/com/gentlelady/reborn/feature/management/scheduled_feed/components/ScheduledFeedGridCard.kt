package com.gentlelady.reborn.feature.management.scheduled_feed.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.OverflowAction
import com.gentlelady.reborn.core.designsystem.components.OverflowActionMenu
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.img_memorial_bg_dummy
import com.gentlelady.reborn.management.scheduled_feed.domain.model.ScheduledFeedItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ScheduledFeedGridCard(
    item: ScheduledFeedItem,
    actions: List<OverflowAction>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Image(
            painter = painterResource(item.thumbnail),
            contentDescription = item.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.1f)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.title,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                OverflowActionMenu(actions = actions)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.scheduleLabel,
                color = RebornCobaltBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
private fun ScheduledFeedGridCardPreview() {
    MaterialTheme {
        ScheduledFeedGridCard(
            item = ScheduledFeedItem(
                id = "1",
                title = "가을 산책을 하며",
                scheduleLabel = "사망 후 일주일 뒤 예약됨",
                thumbnail = Res.drawable.img_memorial_bg_dummy
            ),
            actions = listOf(
                OverflowAction("수정") {},
                OverflowAction("삭제", isDestructive = true) {},
                OverflowAction("지금 올리기") {}
            ),
            modifier = Modifier.width(160.dp)
        )
    }
}
