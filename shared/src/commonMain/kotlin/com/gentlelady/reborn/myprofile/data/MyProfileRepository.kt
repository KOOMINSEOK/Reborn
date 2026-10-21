package com.gentlelady.reborn.myprofile.data

import com.gentlelady.reborn.core.network.ImageUploader
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.MyProfileDto
import com.gentlelady.reborn.core.network.dto.PostDto
import com.gentlelady.reborn.core.network.dto.toAbsoluteServerUrl
import com.gentlelady.reborn.myprofile.domain.model.MyProfileFeedItem
import com.gentlelady.reborn.core.network.dto.UpdateProfileDto

interface MyProfileRepository {
    /** 서버의 내 프로필. 서버 꺼짐/로그인 안 함 → null (화면은 목업 유지). */
    suspend fun getMyProfile(): MyProfileDto?

    /** 사진(JPEG)을 올리고 내 아바타로 저장. 쓰기는 mock 폴백 없음 — 실패 시 예외. */
    suspend fun updateAvatar(jpeg: ByteArray): MyProfileDto

    /** 내 글 최신순 [limit] 개 (프로필 피드 카드). 실패 시 예외. */
    suspend fun myPosts(userId: String, limit: Int): List<MyProfileFeedItem>

    /** 닉네임·한 줄 소개 저장 (null 인 쪽은 안 바꿈). 실패 시 예외. */
    suspend fun updateText(displayName: String?, bio: String?): MyProfileDto
}

class MyProfileRepositoryImpl(
    private val api: RebornApi,
    private val uploader: ImageUploader,
) : MyProfileRepository {

    override suspend fun getMyProfile(): MyProfileDto? = runCatching { api.myProfile() }.getOrNull()

    override suspend fun updateAvatar(jpeg: ByteArray): MyProfileDto {
        val url = uploader.upload(listOf(jpeg)).single()
        return api.updateMyProfile(UpdateProfileDto(avatarUrl = url))
    }

    override suspend fun myPosts(userId: String, limit: Int): List<MyProfileFeedItem> =
        api.userPosts(userId, limit = limit).items.map { it.toFeedItem() }

    override suspend fun updateText(displayName: String?, bio: String?): MyProfileDto =
        api.updateMyProfile(UpdateProfileDto(displayName = displayName, bio = bio))
}

/** 카드 제목 = 본문 첫 줄, 부제 = 나머지 본문 (없으면 날짜). */
private fun PostDto.toFeedItem(): MyProfileFeedItem {
    val lines = caption.trim().lines().filter { it.isNotBlank() }
    return MyProfileFeedItem(
        id = id,
        title = lines.firstOrNull() ?: createdAt.take(10),
        subtitle = lines.drop(1).joinToString(" ").ifBlank { createdAt.take(10) },
        imageUrl = imageUrls.firstOrNull().toAbsoluteServerUrl(),
        likes = likeCount,
        comments = commentCount,
    )
}
