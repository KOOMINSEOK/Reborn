package com.gentlelady.reborn.core.network.dto

import kotlinx.serialization.Serializable

/** 서버 `POST /uploads` 요청. */
@Serializable
data class CreateUploadDto(val contentType: String)

/** [uploadUrl] 에 바이트를 PUT 한 뒤 [imageUrl] 을 글의 imageUrls 에 넣는다. */
@Serializable
data class UploadDto(val uploadUrl: String, val imageUrl: String)

/** 서버 `POST /posts` 요청. publishAt(ISO-8601) 이 있으면 예약 발행. */
@Serializable
data class CreatePostDto(
    val caption: String,
    val imageUrls: List<String>,
    val isPosthumous: Boolean,
    val publishAt: String? = null,
)
