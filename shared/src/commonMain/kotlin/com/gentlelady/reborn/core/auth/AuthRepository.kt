package com.gentlelady.reborn.core.auth

import kotlinx.datetime.Clock

private fun nowEpochSeconds(): Long = Clock.System.now().epochSeconds

class AuthRepository(
    private val api: SupabaseAuthApi,
    private val store: SessionStore,
    private val clock: () -> Long = ::nowEpochSeconds,
) {
    suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        val session = api.signInWithPassword(email.trim(), password).toSession(clock())
        store.current = session
    }

    suspend fun requestEmailCode(email: String) = api.sendEmailOtp(email.trim())

    /** 인증번호가 맞으면 로그인된다 (새 이메일이면 계정이 생긴다). */
    suspend fun verifyEmailCode(email: String, code: String) {
        store.current = api.verifyEmailOtp(email.trim(), code).toSession(clock())
    }

    suspend fun setPassword(password: String) {
        val token = store.current?.accessToken ?: error("not signed in")
        api.setPassword(token, password)
    }

    fun signOut() {
        store.current = null
    }
}
