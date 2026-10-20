package com.gentlelady.reborn.feedwrite.domain.repository

import kotlinx.datetime.Instant

interface FeedWriteRepository {
    /** 이미지(JPEG)를 모두 올리고 서버 이미지 URL 을 같은 순서로 돌려준다. 실패 시 예외. */
    suspend fun uploadImages(images: List<ByteArray>): List<String>

    /**
     * 게시글을 만든다. [publishAt] 이 있으면 예약 발행. 실패 시 예외.
     * [clientRequestId] 가 같으면 서버가 새 글을 만들지 않고 기존 글을 돌려준다 (재시도 중복 방지).
     */
    suspend fun createPost(
        caption: String,
        imageUrls: List<String>,
        isPosthumous: Boolean,
        publishAt: Instant?,
        clientRequestId: String,
    )
}

/** 서버가 사진을 더 이상 붙일 수 없다고 함(오래됐거나 정리됨). 사진을 다시 올려야 한다. */
class ImagesExpiredException : Exception("images expired")
