package com.gentlelady.reborn.home.data.repository

import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.toHomePost
import com.gentlelady.reborn.data.HomeMockData
import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.home.domain.repository.HomeRepository
import kotlinx.coroutines.CancellationException

class HomeRepositoryImpl(
    private val api: RebornApi,
) : HomeRepository {

    // 서버 꺼짐/에러/토큰없음 → mock 으로 폴백. 로그인이 붙으면 실데이터가 흐른다.
    override suspend fun getHomeFeed(): List<HomePost> =
        try {
            api.feed().items.map { it.toHomePost() }
        } catch (e: CancellationException) {
            throw e // 새 요청에 밀려 취소된 것 — 목업으로 바꾸지 않는다
        } catch (e: Exception) {
            HomeMockData.feed
        }
}
