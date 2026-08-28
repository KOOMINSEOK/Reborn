package com.gentlelady.reborn.core.auth

import com.gentlelady.reborn.core.network.TokenProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

/**
 * 저장된 세션의 액세스 토큰을 준다. 만료가 임박하면 refresh_token 으로 갱신하고,
 * 갱신 실패하면 세션을 비운다(로그아웃) → 레포지토리는 mock 으로 폴백.
 */
class SessionTokenProvider(
    private val store: SessionStore,
    private val api: SupabaseAuthApi,
    private val clock: () -> Long = { Clock.System.now().epochSeconds },
) : TokenProvider {

    private val mutex = Mutex()

    override suspend fun accessToken(): String? = mutex.withLock {
        val session = store.current ?: return null
        if (!session.isExpiringSoon(clock())) return session.accessToken

        val refreshed = runCatching { api.refresh(session.refreshToken).toSession(clock()) }.getOrNull()
        store.current = refreshed
        refreshed?.accessToken
    }
}
