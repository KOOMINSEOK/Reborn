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

    fun signOut() {
        store.current = null
    }
}
