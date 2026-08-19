package com.gentlelady.reborn.management.scheduled_feed.domain.model

import org.jetbrains.compose.resources.DrawableResource

/**
 * 사후 공개되도록 예약된 피드 한 건.
 */
data class ScheduledFeedItem(
    val id: String,
    val title: String,
    val scheduleLabel: String, // 예: "사망 후 일주일 뒤 예약됨", "2029/08/15 예약됨"
    val thumbnail: DrawableResource
)
