package com.gentlelady.reborn.feature.management.scheduled_feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.OverflowAction
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.feature.management.scheduled_feed.components.ScheduledFeedGridCard
import com.gentlelady.reborn.feature.management.scheduled_feed.components.ScheduledReminderRow
import com.gentlelady.reborn.img_memorial_bg_dummy
import com.gentlelady.reborn.img_memorial_profile_dummy
import com.gentlelady.reborn.management.scheduled_feed.domain.model.ScheduledFeedItem
import com.gentlelady.reborn.management.scheduled_feed.domain.model.ScheduledReminderItem
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedAction
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedIntent
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedState
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledReminderAction
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScheduledFeedScreen(
    state: ScheduledFeedState,
    onIntent: (ScheduledFeedIntent) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(
                title = "예약된 피드 및 리마인드",
                onBackClick = { onIntent(ScheduledFeedIntent.ClickBack) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            item {
                Text(
                    text = "예약된 사후 피드",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }

            items(state.feeds.chunked(2)) { rowFeeds ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowFeeds.forEach { feed ->
                        ScheduledFeedGridCard(
                            item = feed,
                            actions = listOf(
                                OverflowAction("수정") {
                                    onIntent(ScheduledFeedIntent.ClickFeedAction(feed.id, ScheduledFeedAction.EDIT))
                                },
                                OverflowAction("삭제", isDestructive = true) {
                                    onIntent(ScheduledFeedIntent.ClickFeedAction(feed.id, ScheduledFeedAction.DELETE))
                                },
                                OverflowAction("지금 올리기") {
                                    onIntent(ScheduledFeedIntent.ClickFeedAction(feed.id, ScheduledFeedAction.PUBLISH_NOW))
                                }
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowFeeds.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    thickness = 1.dp,
                    color = RebornDividerGray
                )
                Text(
                    text = "예약된 리마인드",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            items(state.reminders, key = { it.id }) { reminder ->
                ScheduledReminderRow(
                    item = reminder,
                    actions = listOf(
                        OverflowAction("수정") {
                            onIntent(ScheduledFeedIntent.ClickReminderAction(reminder.id, ScheduledReminderAction.EDIT))
                        },
                        OverflowAction("삭제", isDestructive = true) {
                            onIntent(ScheduledFeedIntent.ClickReminderAction(reminder.id, ScheduledReminderAction.DELETE))
                        },
                        OverflowAction("지금 보내기") {
                            onIntent(ScheduledFeedIntent.ClickReminderAction(reminder.id, ScheduledReminderAction.SEND_NOW))
                        }
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// --- 프리뷰 규칙 준수: PreviewParameterProvider 차단 및 Direct Injection 데이터 적용 ---
@Preview
@Composable
private fun ScheduledFeedScreenPreview() {
    val previewState = ScheduledFeedState(
        feeds = listOf(
            ScheduledFeedItem("1", "가을 산책을 하며", "사망 후 일주일 뒤 예약됨", Res.drawable.img_memorial_bg_dummy),
            ScheduledFeedItem("2", "봄날의 인사", "2029/08/15 예약됨", Res.drawable.img_memorial_bg_dummy)
        ),
        reminders = listOf(
            ScheduledReminderItem("1", "박희옥", "사망 후 3일 뒤 예약됨", "늘 감사하고 사랑합니다...", Res.drawable.img_memorial_profile_dummy)
        )
    )
    MaterialTheme {
        ScheduledFeedScreen(state = previewState, onIntent = {})
    }
}
