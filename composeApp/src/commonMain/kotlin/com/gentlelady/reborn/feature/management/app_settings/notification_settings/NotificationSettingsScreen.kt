package com.gentlelady.reborn.feature.management.app_settings.notification_settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornSwitchTrackOff
import com.gentlelady.reborn.management.app_settings.notification_settings.presentation.NotificationSettingsIntent
import com.gentlelady.reborn.management.app_settings.notification_settings.presentation.NotificationSettingsState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NotificationSettingsScreen(
    state: NotificationSettingsState,
    onIntent: (NotificationSettingsIntent) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "알림 설정", onBackClick = { onIntent(NotificationSettingsIntent.ClickBack) })
        }
    ) { paddingValues ->
        val rows = listOf(
            Triple("피드(좋아요, 댓글) 알림", state.feedAlerts) { checked: Boolean ->
                onIntent(NotificationSettingsIntent.ToggleFeedAlerts(checked))
            },
            Triple("메모리얼 히스토리(좋아요, 댓글) 알림", state.historyAlerts) { checked: Boolean ->
                onIntent(NotificationSettingsIntent.ToggleHistoryAlerts(checked))
            },
            Triple("리마인드 도착 알림", state.reminderAlerts) { checked: Boolean ->
                onIntent(NotificationSettingsIntent.ToggleReminderAlerts(checked))
            },
            Triple("방명록 알림", state.guestbookAlerts) { checked: Boolean ->
                onIntent(NotificationSettingsIntent.ToggleGuestbookAlerts(checked))
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            rows.forEachIndexed { index, (label, checked, onCheckedChange) ->
                NotificationToggleRow(label = label, checked = checked, onCheckedChange = onCheckedChange)
                if (index != rows.lastIndex) {
                    HorizontalDivider(thickness = 1.dp, color = RebornGridBorderGray)
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            thumbContent = { Box(modifier = Modifier.size(SwitchDefaults.IconSize)) },
            colors = SwitchDefaults.colors(
                checkedTrackColor = RebornCobaltBlue,
                uncheckedTrackColor = RebornSwitchTrackOff,
                uncheckedThumbColor = Color.White,
                uncheckedBorderColor = RebornSwitchTrackOff
            )
        )
    }
}

@Preview
@Composable
private fun NotificationSettingsScreenPreview() {
    MaterialTheme {
        NotificationSettingsScreen(state = NotificationSettingsState(), onIntent = {})
    }
}
