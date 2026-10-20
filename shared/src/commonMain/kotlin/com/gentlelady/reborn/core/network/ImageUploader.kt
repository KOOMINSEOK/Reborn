package com.gentlelady.reborn.core.network

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * 사진(JPEG) N장을 서버 Storage 에 올리고 이미지 URL 을 같은 순서로 돌려준다. 실패 시 예외.
 * 장마다 업로드 URL 발급(POST /uploads) → Storage 에 직접 PUT 을 병렬로.
 * 게시글·추억 글 등 사진을 올리는 모든 곳이 이걸 쓴다.
 * 끝내 어디에도 안 붙은 사진은 서버의 /internal/cleanup-uploads 가 하루 뒤 지운다.
 */
class ImageUploader(
    private val api: RebornApi,
) {
    suspend fun upload(images: List<ByteArray>): List<String> = coroutineScope {
        images.map { jpeg ->
            async {
                val upload = api.createUpload(contentType = "image/jpeg")
                api.uploadImage(upload.uploadUrl, jpeg)
                upload.imageUrl
            }
        }.awaitAll()
    }
}
