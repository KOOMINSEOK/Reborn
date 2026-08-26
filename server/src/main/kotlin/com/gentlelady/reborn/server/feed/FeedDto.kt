package com.gentlelady.reborn.server.feed

import kotlinx.serialization.Serializable

@Serializable
data class CreateMemorialRequest(
    val name: String,
    val handle: String,
    val bio: String? = null,
    val isPosthumous: Boolean = false,
    val visibility: String = "public",
    val profileImageUrl: String? = null,
)

@Serializable
data class MemorialResponse(
    val id: String,
    val ownerId: String,
    val name: String,
    val handle: String,
    val bio: String? = null,
    val isPosthumous: Boolean,
    val visibility: String,
    val profileImageUrl: String? = null,
    val followerCount: Int,
)

@Serializable
data class CreatePostRequest(
    val memorialId: String,
    val caption: String = "",
    val imageUrl: String? = null,
    val isPosthumous: Boolean = false,
    /** ISO-8601. 지정하면 status=scheduled 로 저장된다. */
    val publishAt: String? = null,
)

@Serializable
data class PostResponse(
    val id: String,
    val memorialId: String,
    val memorialName: String,
    val memorialHandle: String,
    val authorId: String,
    val caption: String,
    val imageUrl: String? = null,
    val isPosthumous: Boolean,
    val status: String,
    val likeCount: Int,
    val commentCount: Int,
    val createdAt: String,
    /** "following" | "recommended" — 이 항목이 피드에 들어온 이유. */
    val source: String = "following",
)

@Serializable
data class FeedResponse(
    val items: List<PostResponse>,
    val nextOffset: Int? = null,
)
