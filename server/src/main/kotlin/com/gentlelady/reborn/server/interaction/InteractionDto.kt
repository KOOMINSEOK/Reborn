package com.gentlelady.reborn.server.interaction

import kotlinx.serialization.Serializable

@Serializable
data class LikeResponse(val liked: Boolean, val likeCount: Int)

@Serializable
data class CreateCommentRequest(val body: String)

@Serializable
data class CommentResponse(
    val id: String,
    val parentId: String,
    val authorId: String,
    val authorName: String,
    val body: String,
    val createdAt: String,
)

@Serializable
data class CommentListResponse(val items: List<CommentResponse>, val nextOffset: Int? = null)
