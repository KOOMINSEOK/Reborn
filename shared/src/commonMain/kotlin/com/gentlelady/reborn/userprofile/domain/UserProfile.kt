package com.gentlelady.reborn.userprofile.domain

import com.gentlelady.reborn.userprofile.data.UserProfileRepository

data class UserProfile(
    val id: String,
    val handle: String,
    val displayName: String,
    val bio: String,
    val avatarUrl: String?,
    val isPrivate: Boolean,
    val followerCount: Int,
    val followingCount: Int,
    val followedByMe: Boolean,
    val blockedByMe: Boolean,
    /** 내 프로필이면 팔로우 버튼을 숨긴다 */
    val isMe: Boolean,
) {
    /** 비공개 계정인데 팔로우 안 함 → 글이 안 보인다 */
    val isLocked: Boolean get() = isPrivate && !followedByMe && !isMe
}

/** 서버가 팔로우를 거절함. [code] = private_account · blocked · user_not_found · rejected */
class FollowRejectedException(val code: String) : Exception("follow rejected: $code")

class GetUserProfileUseCase(private val repository: UserProfileRepository) {
    suspend operator fun invoke(userId: String) = repository.profile(userId)
}

class GetUserPostsUseCase(private val repository: UserProfileRepository) {
    suspend operator fun invoke(userId: String, offset: Int) = repository.posts(userId, offset)
}

class SetFollowUseCase(private val repository: UserProfileRepository) {
    suspend operator fun invoke(userId: String, follow: Boolean) = repository.setFollow(userId, follow)
}
