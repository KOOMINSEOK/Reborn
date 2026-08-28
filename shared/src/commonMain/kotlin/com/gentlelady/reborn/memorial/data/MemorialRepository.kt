package com.gentlelady.reborn.memorial.data

import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.toGuestbookItem
import com.gentlelady.reborn.core.network.dto.toHistoryItem
import com.gentlelady.reborn.core.network.dto.toProfileData
import com.gentlelady.reborn.data.MemorialMockData
import com.gentlelady.reborn.memorial.presentation.MemorialState

interface MemorialRepository {
    /** 프로필 + 히스토리 + 방명록을 한 번에. 어느 하나라도 실패하면 통째로 mock 폴백. */
    suspend fun getMemorial(id: String): MemorialState
}

class MemorialRepositoryImpl(
    private val api: RebornApi,
) : MemorialRepository {

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
