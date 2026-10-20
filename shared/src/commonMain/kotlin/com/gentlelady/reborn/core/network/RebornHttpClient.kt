package com.gentlelady.reborn.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private val apiHost = Url(ApiConfig.BASE_URL).host

fun createRebornHttpClient(tokenProvider: TokenProvider): HttpClient = HttpClient {
    expectSuccess = true

    // 서버가 안 떠 있거나(로컬 개발), 실기기에서 10.0.2.2가 아예 연결이 안 될 때
    // 타임아웃이 없으면 요청이 무한정 걸려 있어 mock 폴백(runCatching)까지 도달하지 못한다.
    install(HttpTimeout) {
        requestTimeoutMillis = 5000
        connectTimeoutMillis = 5000
    }

    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }

    install(Auth) {
        bearer {
            // 우리 서버에만 토큰을 붙인다. Storage 서명 URL(PUT) 에는 사용자 토큰이 새면 안 됨.
            sendWithoutRequest { it.url.host == apiHost }
            loadTokens {
                tokenProvider.accessToken()?.let { BearerTokens(it, refreshToken = "") }
            }
            // 401 이면 TokenProvider 에 다시 물어본다 (세션 갱신/로그인 직후 반영).
            refreshTokens {
                tokenProvider.accessToken()?.let { BearerTokens(it, refreshToken = "") }
            }
        }
    }

    defaultRequest {
        url(ApiConfig.BASE_URL)
        contentType(ContentType.Application.Json)
    }
}
