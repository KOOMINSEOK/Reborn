package com.gentlelady.reborn.core.network.dto

import kotlinx.serialization.Serializable

/** 서버 `GET /feed`, `GET /posts/{id}` 응답. 서버 PostResponse 와 1:1. */
@Serializable
data class PostDto(
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
    val source: String = "following",
)

@Serializable
data class FeedDto(
    val items: List<PostDto>,
    val nextOffset: Int? = null,
)
