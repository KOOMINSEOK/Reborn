package com.gentlelady.reborn.core.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Supabase `/auth/v1/token` 응답. */
@Serializable
data class SessionDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long,
)

data class Session(
    val accessToken: String,
    val refreshToken: String,
    val expiresAtEpochSeconds: Long,
) {
    fun isExpiringSoon(nowEpochSeconds: Long): Boolean =
        nowEpochSeconds >= expiresAtEpochSeconds - REFRESH_MARGIN_SECONDS

    companion object {
        private const val REFRESH_MARGIN_SECONDS = 60L
    }
}

fun SessionDto.toSession(nowEpochSeconds: Long): Session =
    Session(accessToken, refreshToken, expiresAtEpochSeconds = nowEpochSeconds + expiresIn)
