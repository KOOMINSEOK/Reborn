package com.gentlelady.reborn.management.scheduled_feed.domain.model

import org.jetbrains.compose.resources.DrawableResource

/**
 * 사후 특정 시점에 지정한 수신자에게 자동 발송되도록 예약된 리마인드 메시지 한 건.
 */
data class ScheduledReminderItem(
    val id: String,
    val recipientName: String,
    val scheduleLabel: String, // 예: "사망 후 3일 뒤 예약됨"
    val messagePreview: String,
    val recipientAvatar: DrawableResource?
)
