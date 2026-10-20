package com.gentlelady.reborn.core.auth

import com.gentlelady.reborn.core.network.TokenProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

/**
 * 저장된 세션의 액세스 토큰을 준다. 만료가 임박하면 refresh_token 으로 갱신한다.
 * - 서버가 refresh token 을 거부하면(4xx) 세션을 비운다(로그아웃) → 레포지토리는 mock 으로 폴백.
 * - 네트워크 오류·5xx 같은 일시 장애면 세션은 그대로 두고 이번 요청만 토큰 없이 — 다음 요청에서 다시 갱신.
 * - 갱신하는 사이 로그아웃·다른 계정 로그인이 있었으면 결과를 버린다 ([SessionStore.replaceIf]).
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

        val refreshed = try {
            api.refresh(session.refreshToken).toSession(clock())
                // 갱신 응답에 user 가 빠져도 로그인한 사람은 그대로 → 기존 userId 유지
                .let { if (it.userId == null) it.copy(userId = session.userId) else it }
        } catch (e: CancellationException) {
            throw e
        } catch (e: RefreshRejectedException) {
            store.replaceIf(session, null)
            return null
        } catch (e: Exception) {
            return null // 일시 장애 — 세션 유지
        }
        if (store.replaceIf(session, refreshed)) refreshed.accessToken else null
    }
}
