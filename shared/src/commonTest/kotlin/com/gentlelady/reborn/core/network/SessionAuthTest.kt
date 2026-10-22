package com.gentlelady.reborn.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SessionAuthTest {
    @Test
    fun everyRequestUsesTheCurrentSessionToken() = runTest {
        var token: String? = "old"
        val sent = mutableListOf<String?>()
        val client = HttpClient(MockEngine { sent += it.headers[HttpHeaders.Authorization]; respondOk() }) {
            rebornConfig(object : TokenProvider { override suspend fun accessToken() = token })
        }
        client.get("a")
        token = null // 가입 그만두기·로그아웃
        client.get("b")
        token = "new" // 다른 계정으로 로그인
        client.get("c")
        assertEquals(listOf("Bearer old", null, "Bearer new"), sent)
    }
}
