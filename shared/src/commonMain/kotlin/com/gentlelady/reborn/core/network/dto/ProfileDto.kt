package com.gentlelady.reborn.core.network.dto

import kotlinx.serialization.Serializable

/** 서버 `GET|PATCH /me/profile` 응답. 서버 MyProfileResponse 와 1:1. */
@Serializable
data class MyProfileDto(
    val id: String,
    val handle: String,
    val displayName: String,
    val bio: String? = null,
    val avatarUrl: String? = null,
    val isPrivate: Boolean = false,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
)

/** 서버 `GET /users/{id}` 응답 (다른 사람 프로필). */
@Serializable
data class PublicProfileDto(
    val id: String,
    val handle: String,
    val displayName: String,
    val bio: String? = null,
    val avatarUrl: String? = null,
    val isPrivate: Boolean = false,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val followedByMe: Boolean = false,
    val blockedByMe: Boolean = false,
)

/** 서버 팔로워·팔로잉 목록 한 줄. */
@Serializable
data class UserSummaryDto(
    val id: String,
    val handle: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val followedByMe: Boolean = false,
)

@Serializable
data class UserListDto(val items: List<UserSummaryDto>, val nextOffset: Int? = null)

/** 서버 `GET /blocks` 한 줄. */
@Serializable
data class BlockedUserDto(val id: String, val name: String, val handle: String, val avatarUrl: String? = null)

@Serializable
data class BlockedListDto(val items: List<BlockedUserDto>)

/** 서버 `POST|DELETE /users/{id}/follow` 응답. */
@Serializable
data class FollowDto(val following: Boolean, val followerCount: Int)

/** 서버 `PATCH /me/profile` 요청. null 인 필드는 안 바꾼다. */
@Serializable
data class UpdateProfileDto(
    val displayName: String? = null,
    val bio: String? = null,
    val avatarUrl: String? = null,
)
