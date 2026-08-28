package com.gentlelady.reborn.core.network.dto

import com.gentlelady.reborn.memorial.presentation.MemorialGuestBookItem
import com.gentlelady.reborn.memorial.presentation.MemorialHistoryItem
import com.gentlelady.reborn.memorial.presentation.MemorialProfileData
import kotlinx.serialization.Serializable

@Serializable
data class MemorialProfileDto(
    val id: String,
    val creatorId: String,
    val name: String,
    val handle: String,
    val bio: String? = null,
    val visibility: String = "public",
    val profileImageUrl: String? = null,
    val followerCount: Int,
)

@Serializable
data class HistoryDto(
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
data class HistoryListDto(val items: List<HistoryDto>, val nextOffset: Int? = null)

@Serializable
data class GuestbookEntryDto(
    val id: String,
    val memorialId: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val message: String,
    val createdAt: String,
)

@Serializable
data class GuestbookListDto(val items: List<GuestbookEntryDto>, val nextOffset: Int? = null)

fun MemorialProfileDto.toProfileData(): MemorialProfileData = MemorialProfileData(
    id = id,
    name = name,
    handle = handle,
    bio = bio.orEmpty(),
    followerCount = followerCount,
    profileImageUrl = profileImageUrl.toAbsoluteServerUrl(),
)

fun HistoryDto.toHistoryItem(): MemorialHistoryItem = MemorialHistoryItem(
    id = id,
    authorName = authorName,
    date = createdAt.take(10),
    caption = caption,
    imageUrl = imageUrl.toAbsoluteServerUrl(),
    likes = likeCount,
    comments = commentCount,
)

fun GuestbookEntryDto.toGuestbookItem(): MemorialGuestBookItem = MemorialGuestBookItem(
    id = id,
    authorName = authorName,
    authorProfileUrl = authorAvatarUrl.toAbsoluteServerUrl(),
    message = message,
    timestamp = createdAt.take(10),
)
