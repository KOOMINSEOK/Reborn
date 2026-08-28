package com.gentlelady.reborn.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createRebornHttpClient(tokenProvider: TokenProvider): HttpClient = HttpClient {
    expectSuccess = true

    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }

    install(Auth) {
        bearer {
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
