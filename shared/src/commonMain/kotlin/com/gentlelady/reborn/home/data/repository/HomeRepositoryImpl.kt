package com.gentlelady.reborn.home.data.repository

import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.toHomePost
import com.gentlelady.reborn.data.HomeMockData
import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.home.domain.repository.HomeRepository

class HomeRepositoryImpl(
    private val api: RebornApi,
) : HomeRepository {

    // 서버 꺼짐/에러/토큰없음 → mock 으로 폴백. 로그인이 붙으면 실데이터가 흐른다.
    override suspend fun getHomeFeed(): List<HomePost> =
        runCatching { api.feed().items.map { it.toHomePost() } }
            .getOrElse { HomeMockData.feed }
}
