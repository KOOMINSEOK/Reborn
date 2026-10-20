package com.gentlelady.reborn.memorial.data

import com.gentlelady.reborn.core.network.ImageUploader
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.CreateHistoryDto
import com.gentlelady.reborn.core.network.dto.toGuestbookItem
import com.gentlelady.reborn.core.network.dto.toHistoryItem
import com.gentlelady.reborn.core.network.dto.toProfileData
import com.gentlelady.reborn.data.MemorialMockData
import com.gentlelady.reborn.memorial.presentation.MemorialHistoryItem
import com.gentlelady.reborn.memorial.presentation.MemorialState

interface MemorialRepository {
    /** 프로필 + 히스토리 + 방명록을 한 번에. 어느 하나라도 실패하면 통째로 mock 폴백. */
    suspend fun getMemorial(id: String): MemorialState

    /** 사진(JPEG)을 올리고 추억 글을 만든다. 쓰기는 mock 폴백 없음 — 실패 시 예외. */
    suspend fun createHistory(memorialId: String, caption: String, image: ByteArray): MemorialHistoryItem
}

class MemorialRepositoryImpl(
    private val api: RebornApi,
    private val uploader: ImageUploader,
) : MemorialRepository {

    // ponytail: 멱등성 키 없음(게시글과 달리) — 응답 유실 후 재시도하면 추억 글이 하나 더 생길 수 있다. 필요해지면 V11 처럼 추가.
    override suspend fun createHistory(memorialId: String, caption: String, image: ByteArray): MemorialHistoryItem {
        val imageUrls = uploader.upload(listOf(image))
        return api.createHistory(memorialId, CreateHistoryDto(caption = caption, imageUrls = imageUrls)).toHistoryItem()
    }

    override suspend fun getMemorial(id: String): MemorialState = runCatching {
        val profile = api.memorial(id)
        val history = api.memorialHistory(id).items
        val guestbook = api.guestbook(id).items
        MemorialState(
            memorialId = id,
            profile = profile.toProfileData(),
            historyItems = history.map { it.toHistoryItem() },
            guestBookMessages = guestbook.map { it.toGuestbookItem() },
            // 화환은 아직 서버 엔드포인트가 없어 mock 유지.
            onlineWreathItems = MemorialMockData.memorialState.onlineWreathItems,
        )
    }.getOrElse { MemorialMockData.memorialState.copy(memorialId = id) }
}
