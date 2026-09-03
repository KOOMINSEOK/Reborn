package com.gentlelady.reborn.server.profile

import kotlinx.serialization.Serializable

/** null 인 필드는 안 바꾼다 (부분 업데이트). */
@Serializable
data class UpdateProfileRequest(
    val displayName: String? = null,
    val handle: String? = null,
    val bio: String? = null,
    val avatarUrl: String? = null,
    val isPrivate: Boolean? = null,
)

@Serializable
data class MyProfileResponse(
    val id: String,
    val handle: String,
    val displayName: String,
    val bio: String? = null,
    val avatarUrl: String? = null,
    val isPrivate: Boolean,
    val followerCount: Int,
    val followingCount: Int,
)

@Serializable
data class PublicProfileResponse(
    val id: String,
    val handle: String,
    val displayName: String,
    val bio: String? = null,
    val avatarUrl: String? = null,
    val isPrivate: Boolean,
    val followerCount: Int,
    val followingCount: Int,
    val followedByMe: Boolean,
    val blockedByMe: Boolean,
)

@Serializable
data class UserSummaryResponse(
    val id: String,
    val handle: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val followedByMe: Boolean,
)

@Serializable
data class UserListResponse(val items: List<UserSummaryResponse>, val nextOffset: Int? = null)
