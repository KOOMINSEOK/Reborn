package com.gentlelady.reborn.feedwrite.data.repository

import com.gentlelady.reborn.core.network.ImageUploader
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.CreatePostDto
import com.gentlelady.reborn.feedwrite.domain.repository.FeedWriteRepository
import com.gentlelady.reborn.feedwrite.domain.repository.ImagesExpiredException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.Instant

/** 쓰기는 mock 폴백 없음 — 실패하면 그대로 예외를 올려 화면이 알린다. */
class FeedWriteRepositoryImpl(
    private val api: RebornApi,
    private val uploader: ImageUploader,
) : FeedWriteRepository {

    override suspend fun uploadImages(images: List<ByteArray>): List<String> = uploader.upload(images)

    override suspend fun createPost(
        caption: String,
        imageUrls: List<String>,
        isPosthumous: Boolean,
        publishAt: Instant?,
        clientRequestId: String,
    ) {
        try {
            api.createPost(
                CreatePostDto(
                    caption = caption,
                    imageUrls = imageUrls,
                    isPosthumous = isPosthumous,
                    publishAt = publishAt?.toString(),
                    clientRequestId = clientRequestId,
                )
            )
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.BadRequest && "image_expired" in e.response.bodyAsText()) {
                throw ImagesExpiredException()
            }
            throw e
        }
    }
}
