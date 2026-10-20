package com.gentlelady.reborn.myprofile.data

import com.gentlelady.reborn.core.network.ImageUploader
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.MyProfileDto
import com.gentlelady.reborn.core.network.dto.UpdateProfileDto

interface MyProfileRepository {
    /** 서버의 내 프로필. 서버 꺼짐/로그인 안 함 → null (화면은 목업 유지). */
    suspend fun getMyProfile(): MyProfileDto?

    /** 사진(JPEG)을 올리고 내 아바타로 저장. 쓰기는 mock 폴백 없음 — 실패 시 예외. */
    suspend fun updateAvatar(jpeg: ByteArray): MyProfileDto

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

    override suspend fun updateText(displayName: String?, bio: String?): MyProfileDto =
        api.updateMyProfile(UpdateProfileDto(displayName = displayName, bio = bio))
}
