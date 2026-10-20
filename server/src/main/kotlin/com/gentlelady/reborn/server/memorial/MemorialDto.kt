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
    val imageUrls: List<String> = emptyList(),
)

@Serializable
data class HistoryResponse(
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
data class GuestbookListResponse(
    val items: List<GuestbookEntryResponse>,
    val nextOffset: Int? = null,
    /** 더 이전 글이 있으면 다음 요청의 ?cursor= 값. 마지막 글의 (작성 시각, id) — 그 글이 지워져도 유효. */
    val nextCursor: String? = null,
    /** 더 최신 글이 있으면 다음 요청의 ?after= 값 (?around= / ?after= 응답에서만). null = 최신까지 다 받음. */
    val newerCursor: String? = null,
)

/** 내가 남긴 방명록 한 건 + 어느 추모 페이지인지. 누르면 그 페이지 방명록의 이 글 위치로 이동한다. */
@Serializable
data class MyGuestbookEntryResponse(
    val id: String,
    val memorialId: String,
    val memorialName: String,
    val memorialProfileImageUrl: String? = null,
    val message: String,
    val createdAt: String,
)

@Serializable
data class MyGuestbookListResponse(val items: List<MyGuestbookEntryResponse>, val nextCursor: String? = null)
