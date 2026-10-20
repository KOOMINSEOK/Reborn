package com.gentlelady.reborn.message.data

import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.MyGuestbookEntryDto
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

    // ponytail: 끝까지 한 번에 이어 받는다 (최대 MAX_MY_GUESTBOOKS). 수백 개를 넘게 쓰는 사용자가 생기면 화면 무한 스크롤로.
    override suspend fun getMyGuestbooks(): List<GuestBookItem> = runCatching {
        val all = mutableListOf<MyGuestbookEntryDto>()
        var cursor: String? = null
        do {
            val page = api.myGuestbook(cursor)
            all += page.items
            cursor = page.nextCursor
        } while (cursor != null && all.size < MAX_MY_GUESTBOOKS)
        all.map {
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

private const val MAX_MY_GUESTBOOKS = 500
