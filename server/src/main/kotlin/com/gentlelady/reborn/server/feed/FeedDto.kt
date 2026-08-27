package com.gentlelady.reborn.server.feed

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostRequest(
    val caption: String = "",
    val imageUrl: String? = null,
    /** false=생전, true=생후. */
    val isPosthumous: Boolean = false,
    /** ISO-8601. 지정하면 status=scheduled (사후 발행 예약). */
    val publishAt: String? = null,
)

@Serializable
data class PostResponse(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarUrl: String? = null,
    val caption: String,
    val imageUrl: String? = null,
    val isPosthumous: Boolean,
    val status: String,
    val likeCount: Int,
    val commentCount: Int,
    val liked: Boolean = false,
    val createdAt: String,
    /** "following" | "recommended" — 피드에 들어온 이유. */
    val source: String = "following",
)

@Serializable
data class FeedResponse(val items: List<PostResponse>, val nextOffset: Int? = null)
