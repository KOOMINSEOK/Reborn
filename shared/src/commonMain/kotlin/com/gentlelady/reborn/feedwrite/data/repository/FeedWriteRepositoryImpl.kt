package com.gentlelady.reborn.feedwrite.data.repository

import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.CreatePostDto
import com.gentlelady.reborn.feedwrite.domain.repository.FeedWriteRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Instant

/**
 * 사진 N장: 업로드 URL 발급 → Storage 에 직접 PUT 을 장마다 병렬로, 끝나면 순서대로 imageUrls 를 모아 글 작성.
 * 쓰기는 mock 폴백 없음 — 실패하면 그대로 예외를 올려 화면이 알린다.
 * ponytail: 글 작성이 실패해도 이미 올라간 사진은 Storage 에 남는다(고아 파일). 서버 쪽 정리 작업 때 함께 처리.
 */
class FeedWriteRepositoryImpl(
    private val api: RebornApi,
) : FeedWriteRepository {

    override suspend fun createPost(
        caption: String,
        images: List<ByteArray>,
        isPosthumous: Boolean,
        publishAt: Instant?,
    ) {
        val imageUrls = coroutineScope {
            images.map { jpeg ->
                async {
                    val upload = api.createUpload(contentType = "image/jpeg")
                    api.uploadImage(upload.uploadUrl, jpeg)
                    upload.imageUrl
                }
            }.awaitAll()
        }
        api.createPost(
            CreatePostDto(
                caption = caption,
                imageUrls = imageUrls,
                isPosthumous = isPosthumous,
                publishAt = publishAt?.toString(),
            )
        )
    }
}
