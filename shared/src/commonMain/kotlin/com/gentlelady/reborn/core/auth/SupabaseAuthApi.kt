package com.gentlelady.reborn.core.auth

import com.gentlelady.reborn.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class PasswordGrant(val email: String, val password: String)

@Serializable
private data class RefreshGrant(@SerialName("refresh_token") val refreshToken: String)

/** Supabase GoTrue 직접 호출. 여기서 나온 토큰이 RebornApi 호출에 쓰인다. */
class SupabaseAuthApi(private val client: HttpClient) {

    suspend fun signInWithPassword(email: String, password: String): SessionDto =
        token("password") { setBody(PasswordGrant(email, password)) }

    /**
     * refresh token 으로 새 세션. 서버가 토큰을 거부하면(4xx: 만료·폐기) [RefreshRejectedException] —
     * 그때만 로그아웃해야 한다. 네트워크 오류·5xx 는 다른 예외로 → 세션을 지키고 다음에 다시 시도.
     */
    suspend fun refresh(refreshToken: String): SessionDto {
        val response = request("refresh_token") { setBody(RefreshGrant(refreshToken)) }
        if (response.status.value in CLIENT_ERRORS) throw RefreshRejectedException(response.status.value)
        check(response.status.isSuccess()) { "token refresh unavailable: ${response.status}" }
        return response.body()
    }

    private suspend fun token(grantType: String, block: io.ktor.client.request.HttpRequestBuilder.() -> Unit): SessionDto =
        request(grantType, block).body()

    private suspend fun request(
        grantType: String,
        block: io.ktor.client.request.HttpRequestBuilder.() -> Unit,
    ): HttpResponse =
        client.post("${ApiConfig.SUPABASE_URL}/auth/v1/token?grant_type=$grantType") {
            header("apikey", ApiConfig.SUPABASE_ANON_KEY)
            contentType(ContentType.Application.Json)
            block()
        }
}

/** 서버가 refresh token 을 확정적으로 거부함 (만료·폐기·다른 기기에서 로그아웃 등). */
class RefreshRejectedException(val status: Int) : Exception("refresh token rejected: $status")

private val CLIENT_ERRORS = 400..499
