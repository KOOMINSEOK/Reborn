package com.gentlelady.reborn.feedwrite.domain.repository

import kotlinx.datetime.Instant

interface FeedWriteRepository {
    /** 이미지(JPEG)를 모두 올린 뒤 게시글을 만든다. [publishAt] 이 있으면 예약 발행. 실패 시 예외. */
    suspend fun createPost(caption: String, images: List<ByteArray>, isPosthumous: Boolean, publishAt: Instant?)
}
