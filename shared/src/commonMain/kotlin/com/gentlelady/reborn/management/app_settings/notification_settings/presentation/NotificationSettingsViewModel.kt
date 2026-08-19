package com.gentlelady.reborn.management.app_settings.notification_settings.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationSettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(NotificationSettingsState())
    val state: StateFlow<NotificationSettingsState> = _state.asStateFlow()

    fun handleIntent(intent: NotificationSettingsIntent) {
        when (intent) {
            is NotificationSettingsIntent.ClickBack -> { /* 상위 네비게이션에서 처리 */ }
            is NotificationSettingsIntent.ToggleFeedAlerts -> _state.update { it.copy(feedAlerts = intent.enabled) }
            is NotificationSettingsIntent.ToggleHistoryAlerts -> _state.update { it.copy(historyAlerts = intent.enabled) }
            is NotificationSettingsIntent.ToggleReminderAlerts -> _state.update { it.copy(reminderAlerts = intent.enabled) }
            is NotificationSettingsIntent.ToggleGuestbookAlerts -> _state.update { it.copy(guestbookAlerts = intent.enabled) }
        }
    }
}
