package com.gentlelady.reborn.management.app_settings.notification_settings.presentation

data class NotificationSettingsState(
    val feedAlerts: Boolean = true, // 피드(좋아요, 댓글) 알림
    val historyAlerts: Boolean = true, // 메모리얼 히스토리(좋아요, 댓글) 알림
    val reminderAlerts: Boolean = true, // 리마인드 도착 알림
    val guestbookAlerts: Boolean = true // 방명록 알림
)

sealed interface NotificationSettingsIntent {
    object ClickBack : NotificationSettingsIntent
    data class ToggleFeedAlerts(val enabled: Boolean) : NotificationSettingsIntent
    data class ToggleHistoryAlerts(val enabled: Boolean) : NotificationSettingsIntent
    data class ToggleReminderAlerts(val enabled: Boolean) : NotificationSettingsIntent
    data class ToggleGuestbookAlerts(val enabled: Boolean) : NotificationSettingsIntent
}
