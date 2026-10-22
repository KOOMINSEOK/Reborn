package com.gentlelady.reborn.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.bearerAuth
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createRebornHttpClient(tokenProvider: TokenProvider): HttpClient = HttpClient { rebornConfig(tokenProvider) }

internal fun io.ktor.client.HttpClientConfig<*>.rebornConfig(tokenProvider: TokenProvider) {
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

    // 요청마다 지금 세션의 토큰을 붙인다. Ktor bearer 플러그인은 처음 받은 토큰을 401 전까지 계속 써서,
    // 로그아웃·가입 그만두기 뒤 다른 계정으로 로그인해도 지워진 계정 토큰이 나갔다.
    install(createClientPlugin("SessionAuth") {
        onRequest { request, _ ->
            if (request.headers[HttpHeaders.Authorization] == null) {
                tokenProvider.accessToken()?.let { request.bearerAuth(it) }
            }
        }
    })

    defaultRequest {
        url(ApiConfig.BASE_URL)
        contentType(ContentType.Application.Json)
    }
}

/** Storage 서명 URL 업로드 전용. 인증·기본 URL 없음 (토큰 유출 방지). 큰 파일이라 타임아웃은 요청마다 지정. */
fun createUploadHttpClient(): HttpClient = HttpClient {
    expectSuccess = true
    install(HttpTimeout)
}
