package com.gentlelady.reborn.core.auth

import com.gentlelady.reborn.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
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

@Serializable
private data class OtpRequest(val email: String, @SerialName("create_user") val createUser: Boolean = true)

@Serializable
private data class OtpVerify(val type: String, val email: String, val token: String)

@Serializable
private data class PasswordUpdate(val password: String, val data: Map<String, Boolean>)

/** Supabase GoTrue 직접 호출. 여기서 나온 토큰이 RebornApi 호출에 쓰인다. */
class SupabaseAuthApi(private val client: HttpClient) {

    suspend fun signInWithPassword(email: String, password: String): SessionDto =
        token("password") { setBody(PasswordGrant(email, password)) }

    /**
     * 이메일로 6자리 인증번호를 보낸다 (없는 이메일이면 계정을 만든다 — 가입·로그인 공용).
     * 메일 발송 한도에 걸리면 [EmailRateLimitedException].
     * 메일 본문에 번호가 나오려면 Supabase 메일 템플릿에 {{ .Token }} 이 있어야 한다.
     */
    suspend fun sendEmailOtp(email: String) {
        val response = client.post("${ApiConfig.SUPABASE_URL}/auth/v1/otp") {
            header("apikey", ApiConfig.SUPABASE_ANON_KEY)
            contentType(ContentType.Application.Json)
            setBody(OtpRequest(email))
        }
        if (response.status.value == 429) throw EmailRateLimitedException()
        check(response.status.isSuccess()) { "email otp failed: ${response.status}" }
    }

    /** 인증번호 확인 → 세션. 틀렸거나 만료면 [InvalidCodeException]. */
    suspend fun verifyEmailOtp(email: String, token: String): SessionDto {
        val response = client.post("${ApiConfig.SUPABASE_URL}/auth/v1/verify") {
            header("apikey", ApiConfig.SUPABASE_ANON_KEY)
            contentType(ContentType.Application.Json)
            setBody(OtpVerify("email", email, token))
        }
        if (response.status.value in CLIENT_ERRORS) throw InvalidCodeException()
        check(response.status.isSuccess()) { "email verify failed: ${response.status}" }
        return response.body()
    }

    /** 로그인한 계정의 비밀번호를 정한다. 같은 요청에 "정했음" 표시를 남겨 서버가 가입 단계를 안다. */
    suspend fun setPassword(accessToken: String, password: String) {
        val response = client.put("${ApiConfig.SUPABASE_URL}/auth/v1/user") {
            header("apikey", ApiConfig.SUPABASE_ANON_KEY)
            header("Authorization", "Bearer $accessToken")
            contentType(ContentType.Application.Json)
            setBody(PasswordUpdate(password, mapOf("password_set" to true)))
        }
        if (response.status.value == 422) throw WeakPasswordException()
        check(response.status.isSuccess()) { "set password failed: ${response.status}" }
    }

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

class EmailRateLimitedException : Exception("email rate limited")
class InvalidCodeException : Exception("invalid or expired code")
class WeakPasswordException : Exception("password rejected")

/** 서버가 refresh token 을 확정적으로 거부함 (만료·폐기·다른 기기에서 로그아웃 등). */
class RefreshRejectedException(val status: Int) : Exception("refresh token rejected: $status")

private val CLIENT_ERRORS = 400..499
