package com.gentlelady.reborn.memorial.data

import com.gentlelady.reborn.core.auth.SessionStore
import com.gentlelady.reborn.core.network.ImageUploader
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.CreateGuestbookDto
import com.gentlelady.reborn.core.network.dto.CreateHistoryDto
import com.gentlelady.reborn.core.network.dto.toGuestbookItem
import com.gentlelady.reborn.core.network.dto.toHistoryItem
import com.gentlelady.reborn.core.network.dto.toProfileData
import com.gentlelady.reborn.data.MemorialMockData
import com.gentlelady.reborn.memorial.presentation.MemorialGuestBookItem
import com.gentlelady.reborn.memorial.presentation.MemorialHistoryItem
import com.gentlelady.reborn.memorial.presentation.MemorialState
import com.gentlelady.reborn.memorial.presentation.MemorialTab

interface MemorialRepository {
    /**
     * 프로필 + 히스토리 + 방명록을 한 번에. 어느 하나라도 실패하면 통째로 mock 폴백.
     * [focusGuestBookId] 가 있으면 방명록은 그 글을 가운데로 앞뒤를 받는다 (그 글이 지워졌으면 최신 페이지).
     */
    suspend fun getMemorial(id: String, focusGuestBookId: String? = null): MemorialState

    /** 최신 방명록 한 페이지 (화면 순서) + 이전 커서. 중간을 보다가 새 글을 남겼을 때 최신으로 돌아가기용. */
    suspend fun latestGuestbook(memorialId: String): Pair<List<MemorialGuestBookItem>, String?>

    /** [cursor] 이후(최신 쪽) 방명록 한 페이지를 화면 순서로. 두 번째 값 = 다음 최신 커서(null 이면 최신까지 받음). */
    suspend fun loadNewerGuestbook(memorialId: String, cursor: String): Pair<List<MemorialGuestBookItem>, String?>

    /** 사진(JPEG)을 올리고 추억 글을 만든다. 쓰기는 mock 폴백 없음 — 실패 시 예외. */
    suspend fun createHistory(memorialId: String, caption: String, image: ByteArray): MemorialHistoryItem

    /**
     * [cursor] 이전 방명록 한 페이지를 화면 순서(오래된 것 → 최신)로. 두 번째 값 = 다음 커서(null 이면 끝).
     * 읽기지만 mock 폴백 없음 — 실패 시 예외 (이미 보이는 목록을 mock 으로 바꾸면 안 되므로).
     */
    suspend fun loadOlderGuestbook(memorialId: String, cursor: String): Pair<List<MemorialGuestBookItem>, String?>

    /** 방명록을 남긴다. 쓰기는 mock 폴백 없음 — 실패 시 예외. */
    suspend fun createGuestbookEntry(memorialId: String, message: String): MemorialGuestBookItem
}

class MemorialRepositoryImpl(
    private val api: RebornApi,
    private val uploader: ImageUploader,
    private val sessionStore: SessionStore,
) : MemorialRepository {

    private val myUserId: String? get() = sessionStore.current?.userId

    override suspend fun latestGuestbook(memorialId: String): Pair<List<MemorialGuestBookItem>, String?> {
        val page = api.guestbook(memorialId)
        return page.items.map { it.toGuestbookItem(myUserId) }.reversed() to page.nextCursor
    }

    override suspend fun loadNewerGuestbook(
        memorialId: String,
        cursor: String,
    ): Pair<List<MemorialGuestBookItem>, String?> {
        val page = api.guestbookNewer(memorialId, after = cursor)
        return page.items.map { it.toGuestbookItem(myUserId) }.reversed() to page.newerCursor
    }

    override suspend fun loadOlderGuestbook(
        memorialId: String,
        cursor: String,
    ): Pair<List<MemorialGuestBookItem>, String?> {
        val page = api.guestbook(memorialId, cursor = cursor)
        return page.items.map { it.toGuestbookItem(myUserId) }.reversed() to page.nextCursor
    }

    // ponytail: 멱등성 키 없음 — 응답 유실 후 재전송하면 같은 방명록이 하나 더 생길 수 있다.
    override suspend fun createGuestbookEntry(memorialId: String, message: String): MemorialGuestBookItem =
        api.createGuestbookEntry(memorialId, CreateGuestbookDto(message)).toGuestbookItem(myUserId)

    // ponytail: 멱등성 키 없음(게시글과 달리) — 응답 유실 후 재시도하면 추억 글이 하나 더 생길 수 있다. 필요해지면 V11 처럼 추가.
    override suspend fun createHistory(memorialId: String, caption: String, image: ByteArray): MemorialHistoryItem {
        val imageUrls = uploader.upload(listOf(image))
        return api.createHistory(memorialId, CreateHistoryDto(caption = caption, imageUrls = imageUrls)).toHistoryItem()
    }

    override suspend fun getMemorial(id: String, focusGuestBookId: String?): MemorialState = runCatching {
        val profile = api.memorial(id)
        val history = api.memorialHistory(id).items
        val guestbookPage = focusGuestBookId
            ?.let { focus -> runCatching { api.guestbookAround(id, focus) }.getOrNull() }
            ?: api.guestbook(id)
        val focusFound = focusGuestBookId != null && guestbookPage.items.any { it.id == focusGuestBookId }
        val guestbook = guestbookPage.items
        MemorialState(
            memorialId = id,
            profile = profile.toProfileData(),
            historyItems = history.map { it.toHistoryItem() },
            // 서버는 최신순, 화면은 채팅처럼 오래된 것이 위·새 글이 아래 → 뒤집는다
            guestBookMessages = guestbook.map { it.toGuestbookItem(myUserId) }.reversed(),
            olderGuestBookCursor = guestbookPage.nextCursor,
            newerGuestBookCursor = guestbookPage.newerCursor,
            guestBookFocusId = focusGuestBookId?.takeIf { focusFound },
            selectedTab = if (focusFound) MemorialTab.GUESTBOOK else MemorialTab.HISTORY,
            // 화환은 아직 서버 엔드포인트가 없어 mock 유지.
            onlineWreathItems = MemorialMockData.memorialState.onlineWreathItems,
        )
    }.getOrElse { MemorialMockData.memorialState.copy(memorialId = id) }
}
