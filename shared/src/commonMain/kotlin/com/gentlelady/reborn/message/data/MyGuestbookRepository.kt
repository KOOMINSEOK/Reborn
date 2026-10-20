package com.gentlelady.reborn.message.data

import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.toAbsoluteServerUrl
import com.gentlelady.reborn.data.MockDataSource
import com.gentlelady.reborn.message.presentation.GuestBookItem

interface MyGuestbookRepository {
    /** 내가 남긴 방명록 (최신순). 서버 꺼짐/로그인 안 함 → 목업 폴백. */
    suspend fun getMyGuestbooks(): List<GuestBookItem>
}

class MyGuestbookRepositoryImpl(
    private val api: RebornApi,
) : MyGuestbookRepository {

    // ponytail: 최신 30개만. 메시지 탭에서도 더 보기가 필요해지면 nextCursor 로 이어 받는다.
    override suspend fun getMyGuestbooks(): List<GuestBookItem> = runCatching {
        api.myGuestbook().items.map {
            GuestBookItem(
                id = it.id,
                deceasedName = it.memorialName,
                recentContent = it.message,
                relativeTime = it.createdAt.take(10),
                avatarUrl = it.memorialProfileImageUrl.toAbsoluteServerUrl().orEmpty(),
                memorialId = it.memorialId,
            )
        }
    }.getOrElse { MockDataSource.messageGuestBooks }
}
