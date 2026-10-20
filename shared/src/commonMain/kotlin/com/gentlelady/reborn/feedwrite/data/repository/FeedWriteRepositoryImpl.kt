package com.gentlelady.reborn.feedwrite.data.repository

import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.CreatePostDto
import com.gentlelady.reborn.feedwrite.domain.repository.FeedWriteRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Instant

/**
 * 사진 N장: 업로드 URL 발급 → Storage 에 직접 PUT 을 장마다 병렬로, 끝나면 순서대로 imageUrls 를 모은다.
 * 쓰기는 mock 폴백 없음 — 실패하면 그대로 예외를 올려 화면이 알린다.
 * 끝내 글에 안 쓰인 사진은 서버의 /internal/cleanup-uploads 가 하루 뒤 지운다.
 */
class FeedWriteRepositoryImpl(
    private val api: RebornApi,
) : FeedWriteRepository {

    override suspend fun uploadImages(images: List<ByteArray>): List<String> = coroutineScope {
        images.map { jpeg ->
            async {
                val upload = api.createUpload(contentType = "image/jpeg")
                api.uploadImage(upload.uploadUrl, jpeg)
                upload.imageUrl
            }
        }.awaitAll()
    }

    override suspend fun createPost(
        caption: String,
        imageUrls: List<String>,
        isPosthumous: Boolean,
        publishAt: Instant?,
        clientRequestId: String,
    ) {
        api.createPost(
            CreatePostDto(
                caption = caption,
                imageUrls = imageUrls,
                isPosthumous = isPosthumous,
                publishAt = publishAt?.toString(),
                clientRequestId = clientRequestId,
            )
        )
    }
}
