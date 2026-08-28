package com.gentlelady.reborn.core.network

import com.gentlelady.reborn.core.network.dto.FeedDto
import com.gentlelady.reborn.core.network.dto.PostDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/** Reborn 서버 REST 엔드포인트. 호출 실패 시 예외를 던지며, 폴백은 레포지토리 책임. */
class RebornApi(private val client: HttpClient) {

    suspend fun feed(offset: Int = 0, limit: Int = 20): FeedDto =
        client.get("feed") {
            parameter("offset", offset)
            parameter("limit", limit)
        }.body()

    suspend fun post(id: String): PostDto =
        client.get("posts/$id").body()
}
