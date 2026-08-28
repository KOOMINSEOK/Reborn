package com.gentlelady.reborn.server.memorial

import kotlinx.serialization.Serializable

@Serializable
data class CreateMemorialRequest(
    val name: String,
    val handle: String,
    val bio: String? = null,
    val visibility: String = "public",
    val profileImageUrl: String? = null,
)

@Serializable
data class MemorialResponse(
    val id: String,
    val creatorId: String,
    val name: String,
    val handle: String,
    val bio: String? = null,
    val visibility: String,
    val profileImageUrl: String? = null,
    val followerCount: Int,
)

@Serializable
data class CreateHistoryRequest(
    val caption: String = "",
    val imageUrl: String? = null,
)

@Serializable
data class HistoryResponse(
    val id: String,
    val memorialId: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val caption: String,
    val imageUrl: String? = null,
    val likeCount: Int,
    val commentCount: Int,
    val liked: Boolean = false,
    val createdAt: String,
)

@Serializable
data class HistoryListResponse(val items: List<HistoryResponse>, val nextOffset: Int? = null)

@Serializable
data class CreateGuestbookRequest(val message: String)

@Serializable
data class GuestbookEntryResponse(
    val id: String,
    val memorialId: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val message: String,
    val createdAt: String,
)

@Serializable
data class GuestbookListResponse(val items: List<GuestbookEntryResponse>, val nextOffset: Int? = null)
