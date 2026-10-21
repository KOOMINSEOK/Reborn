package com.gentlelady.reborn.core.network.dto

import kotlinx.serialization.Serializable

/** 서버 `GET /notifications` 한 줄. 서버 NotificationResponse 와 1:1. */
@Serializable
data class NotificationDto(
    val id: String,
    val type: String,
    val actorId: String,
    val actorName: String,
    val actorHandle: String = "",
    val actorAvatarUrl: String? = null,
    val postId: String? = null,
    val historyId: String? = null,
    val memorialId: String? = null,
    val memorialName: String? = null,
    val sourceId: String? = null,
    val preview: String? = null,
    val imageUrl: String? = null,
    val createdAt: String,
    val read: Boolean = false,
)

@Serializable
data class NotificationListDto(val items: List<NotificationDto>, val nextCursor: String? = null)

@Serializable
data class UnreadCountDto(val count: Int)
