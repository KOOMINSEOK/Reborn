package com.gentlelady.reborn.core.network.dto

import kotlinx.serialization.Serializable

/** 서버 `POST|DELETE {parent}/{id}/like` 응답. */
@Serializable
data class LikeDto(val liked: Boolean, val likeCount: Int)

@Serializable
data class CommentDto(
    val id: String,
    val parentId: String,
    val authorId: String,
    val authorName: String,
    val body: String,
    val createdAt: String,
)

@Serializable
data class CommentListDto(val items: List<CommentDto>, val nextOffset: Int? = null)

@Serializable
data class CreateCommentDto(val body: String)
