package com.gentlelady.reborn.core.auth

import com.gentlelady.reborn.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
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

    suspend fun refresh(refreshToken: String): SessionDto =
        token("refresh_token") { setBody(RefreshGrant(refreshToken)) }

    private suspend fun token(grantType: String, block: io.ktor.client.request.HttpRequestBuilder.() -> Unit): SessionDto =
        client.post("${ApiConfig.SUPABASE_URL}/auth/v1/token?grant_type=$grantType") {
            header("apikey", ApiConfig.SUPABASE_ANON_KEY)
            contentType(ContentType.Application.Json)
            block()
        }.body()
}
