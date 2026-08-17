package com.gentlelady.reborn.management.scheduled_feed.presentation

import com.gentlelady.reborn.management.scheduled_feed.domain.model.ScheduledFeedItem
import com.gentlelady.reborn.management.scheduled_feed.domain.model.ScheduledReminderItem

data class ScheduledFeedState(
    val feeds: List<ScheduledFeedItem> = emptyList(),
    val reminders: List<ScheduledReminderItem> = emptyList(),
    val isLoading: Boolean = true
)

enum class ScheduledFeedAction { EDIT, DELETE, PUBLISH_NOW }
enum class ScheduledReminderAction { EDIT, DELETE, SEND_NOW }

sealed interface ScheduledFeedIntent {
    object LoadScheduledFeed : ScheduledFeedIntent
    object ClickBack : ScheduledFeedIntent
    data class ClickFeedAction(val feedId: String, val action: ScheduledFeedAction) : ScheduledFeedIntent
    data class ClickReminderAction(val reminderId: String, val action: ScheduledReminderAction) : ScheduledFeedIntent
}
