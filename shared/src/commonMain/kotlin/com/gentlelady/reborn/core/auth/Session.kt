package com.gentlelady.reborn.core.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Supabase `/auth/v1/token` 응답. */
@Serializable
data class SessionDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    val user: SessionUserDto? = null,
)

@Serializable
data class SessionUserDto(val id: String)

/** 기기에 저장하려고 직렬화한다 ([SessionStore] → [SessionStorage]). */
@Serializable
data class Session(
    val accessToken: String,
    val refreshToken: String,
    val expiresAtEpochSeconds: Long,
    /** 로그인한 사용자 id (Supabase auth.users.id). 내 글/방명록 판별에 쓴다. */
    val userId: String? = null,
) {
    fun isExpiringSoon(nowEpochSeconds: Long): Boolean =
        nowEpochSeconds >= expiresAtEpochSeconds - REFRESH_MARGIN_SECONDS

    companion object {
        private const val REFRESH_MARGIN_SECONDS = 60L
    }
}

fun SessionDto.toSession(nowEpochSeconds: Long): Session =
    Session(accessToken, refreshToken, expiresAtEpochSeconds = nowEpochSeconds + expiresIn, userId = user?.id)
