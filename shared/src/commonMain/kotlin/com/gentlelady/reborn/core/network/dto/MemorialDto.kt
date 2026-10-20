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
    val imageUrls: List<String> = emptyList(),
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
data class GuestbookListDto(
    val items: List<GuestbookEntryDto>,
    val nextOffset: Int? = null,
    /** 더 이전 글이 있으면 다음 요청의 cursor. null = 끝. */
    val nextCursor: String? = null,
    /** 더 최신 글이 있으면 다음 요청의 after (around/after 응답). null = 최신까지 받음. */
    val newerCursor: String? = null,
)

/** 서버 `GET /me/guestbook` 한 건 — 내가 남긴 방명록 + 어느 추모 페이지인지. */
@Serializable
data class MyGuestbookEntryDto(
    val id: String,
    val memorialId: String,
    val memorialName: String,
    val memorialProfileImageUrl: String? = null,
    val message: String,
    val createdAt: String,
)

@Serializable
data class MyGuestbookListDto(val items: List<MyGuestbookEntryDto>, val nextCursor: String? = null)

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
    imageUrls = imageUrls.mapNotNull { it.toAbsoluteServerUrl() },
    likes = likeCount,
    comments = commentCount,
)

/** [myUserId] = 로그인한 사용자 id. 같으면 내 방명록(오른쪽 말풍선). */
fun GuestbookEntryDto.toGuestbookItem(myUserId: String?): MemorialGuestBookItem = MemorialGuestBookItem(
    id = id,
    authorName = authorName,
    authorProfileUrl = authorAvatarUrl.toAbsoluteServerUrl(),
    message = message,
    timestamp = createdAt.take(10),
    isMine = myUserId != null && authorId == myUserId,
)
