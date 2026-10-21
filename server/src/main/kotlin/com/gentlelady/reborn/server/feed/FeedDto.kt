package com.gentlelady.reborn.server.feed

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostRequest(
    val caption: String = "",
    /** POST /uploads 로 받은 imageUrl 들. 순서 = 표시 순서, 최대 10장. */
    val imageUrls: List<String> = emptyList(),
    /** false=생전, true=생후. */
    val isPosthumous: Boolean = false,
    /** ISO-8601. 지정하면 status=scheduled (사후 발행 예약). */
    val publishAt: String? = null,
    /** 멱등성 키(UUID). 같은 작성자가 같은 키로 다시 보내면 새 글을 만들지 않고 기존 글을 돌려준다. */
    val clientRequestId: String? = null,
)

@Serializable
data class PostResponse(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarUrl: String? = null,
    val caption: String,
    val imageUrls: List<String> = emptyList(),
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

/** 팔로우/언팔로우 응답 — 앱이 다시 조회하지 않고 버튼·숫자를 맞춘다. */
@Serializable
data class FollowResponse(val following: Boolean, val followerCount: Int)

@Serializable
data class BlockedUserResponse(
    val id: String,
    val name: String,
    val handle: String,
    val avatarUrl: String? = null,
)

@Serializable
data class BlockedListResponse(val items: List<BlockedUserResponse>)
