package com.gentlelady.reborn.userprofile.data

import com.gentlelady.reborn.core.auth.SessionStore
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.toAbsoluteServerUrl
import com.gentlelady.reborn.core.network.dto.toHomePost
import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.userprofile.domain.FollowRejectedException
import com.gentlelady.reborn.userprofile.domain.UserProfile
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

/** 다른 사람 프로필. mock 폴백 없음 — 실패 시 예외 (화면이 오류를 보여준다). */
interface UserProfileRepository {
    suspend fun profile(userId: String): UserProfile

    /** 최신순 한 페이지 + 다음 offset (null 이면 끝). */
    suspend fun posts(userId: String, offset: Int): Pair<List<HomePost>, Int?>

    /** 팔로우/언팔로우 후 서버 기준 (팔로우 중, 팔로워 수). 서버가 거절하면 [FollowRejectedException]. */
    suspend fun setFollow(userId: String, follow: Boolean): Pair<Boolean, Int>
}

class UserProfileRepositoryImpl(
    private val api: RebornApi,
    private val session: SessionStore,
) : UserProfileRepository {

    override suspend fun profile(userId: String): UserProfile = api.userProfile(userId).let {
        UserProfile(
            id = it.id,
            handle = it.handle,
            displayName = it.displayName,
            bio = it.bio.orEmpty(),
            avatarUrl = it.avatarUrl.toAbsoluteServerUrl(),
            isPrivate = it.isPrivate,
            followerCount = it.followerCount,
            followingCount = it.followingCount,
            followedByMe = it.followedByMe,
            blockedByMe = it.blockedByMe,
            isMe = it.id == session.current?.userId,
        )
    }

    override suspend fun posts(userId: String, offset: Int): Pair<List<HomePost>, Int?> =
        api.userPosts(userId, offset, PAGE_SIZE).let { page -> page.items.map { it.toHomePost() } to page.nextOffset }

    override suspend fun setFollow(userId: String, follow: Boolean): Pair<Boolean, Int> {
        try {
            api.setFollow(userId, follow)
        } catch (e: ClientRequestException) {
            val status = e.response.status
            if (status == HttpStatusCode.Forbidden || status == HttpStatusCode.NotFound) {
                val body = e.response.bodyAsText()
                throw FollowRejectedException(REJECT_CODES.firstOrNull { it in body } ?: "rejected")
            }
            throw e
        }
        // 팔로워 수는 남들도 바꾸므로 서버에서 다시 받는다
        return profile(userId).let { it.followedByMe to it.followerCount }
    }

    private companion object {
        const val PAGE_SIZE = 20
        val REJECT_CODES = listOf("private_account", "blocked", "user_not_found")
    }
}
