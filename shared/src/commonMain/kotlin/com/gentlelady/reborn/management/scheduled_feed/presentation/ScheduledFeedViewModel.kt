package com.gentlelady.reborn.management.scheduled_feed.presentation

import androidx.lifecycle.ViewModel
import com.gentlelady.reborn.data.ScheduledFeedMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ScheduledFeedViewModel : ViewModel() {

    private val _state = MutableStateFlow(ScheduledFeedState())
    val state: StateFlow<ScheduledFeedState> = _state.asStateFlow()

    init {
        handleIntent(ScheduledFeedIntent.LoadScheduledFeed)
    }

    fun handleIntent(intent: ScheduledFeedIntent) {
        when (intent) {
            is ScheduledFeedIntent.LoadScheduledFeed -> loadMockData()
            is ScheduledFeedIntent.ClickBack -> { /* 상위 네비게이션에서 처리 */ }
            is ScheduledFeedIntent.ClickFeedAction -> handleFeedAction(intent)
            is ScheduledFeedIntent.ClickReminderAction -> handleReminderAction(intent)
        }
    }

    private fun handleFeedAction(intent: ScheduledFeedIntent.ClickFeedAction) {
        when (intent.action) {
            ScheduledFeedAction.DELETE -> _state.update { s ->
                s.copy(feeds = s.feeds.filterNot { it.id == intent.feedId })
            }
            ScheduledFeedAction.EDIT, ScheduledFeedAction.PUBLISH_NOW -> { /* TODO: 실제 편집/즉시 공개 로직 연동 */ }
        }
    }

    private fun handleReminderAction(intent: ScheduledFeedIntent.ClickReminderAction) {
        when (intent.action) {
            ScheduledReminderAction.DELETE -> _state.update { s ->
                s.copy(reminders = s.reminders.filterNot { it.id == intent.reminderId })
            }
            ScheduledReminderAction.EDIT, ScheduledReminderAction.SEND_NOW -> { /* TODO: 실제 편집/즉시 발송 로직 연동 */ }
        }
    }

    private fun loadMockData() {
        _state.update {
            it.copy(
                // 💡 "사망 후 N일 뒤" 같은 상대 예약이 "YYYY/MM/DD" 지정일 예약보다 앞에 오도록 정렬
                feeds = ScheduledFeedMockData.scheduledFeeds.sortedByDescending { feed -> isRelativeSchedule(feed.scheduleLabel) },
                reminders = ScheduledFeedMockData.scheduledReminders.sortedByDescending { reminder -> isRelativeSchedule(reminder.scheduleLabel) },
                isLoading = false
            )
        }
    }

    private fun isRelativeSchedule(scheduleLabel: String): Boolean = scheduleLabel.contains("후")
}
