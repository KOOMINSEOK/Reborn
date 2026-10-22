package com.gentlelady.reborn.core.auth

import io.ktor.http.encodeURLParameter
import io.ktor.util.Digest
import io.ktor.util.encodeBase64
import io.ktor.util.generateNonce
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

    /** PKCE: 로그인 창을 연 쪽만 코드를 세션으로 바꿀 수 있게. ponytail: 메모리에만 — 그 사이 앱이 죽으면 다시 시도 */
    private var pkceVerifier: String? = null

    /** 브라우저로 열 소셜 로그인 주소 (Supabase 가 직접 지원하는 곳 — 카카오·Google) */
    suspend fun oauthUrl(provider: String): String {
        val verifier = generateNonce() + generateNonce()
        pkceVerifier = verifier
        val sha256 = Digest("SHA-256").apply { plusAssign(verifier.encodeToByteArray()) }
        val challenge = sha256.build().encodeBase64()
            .trimEnd('=').replace('+', '-').replace('/', '_')
        return "${com.gentlelady.reborn.core.network.ApiConfig.SUPABASE_URL}/auth/v1/authorize" +
            "?provider=$provider&redirect_to=${OAUTH_REDIRECT.encodeURLParameter()}" +
            "&code_challenge=$challenge&code_challenge_method=s256" +
            // 시작 화면에서 고른 거니 매번 계정을 고르게 (전에 로그인한 계정으로 몰래 넘어가지 않게 — 그만두기 뒤 다른 계정으로)
            "&prompt=select_account"
    }

    /** 로그인 창에서 돌아온 코드로 로그인. 연 적 없는 창의 코드면 [InvalidCodeException] */
    suspend fun completeOAuth(code: String) {
        val verifier = pkceVerifier ?: throw InvalidCodeException()
        store.current = api.exchangeCode(code, verifier).toSession(clock())
        pkceVerifier = null
    }

    /** 서버가 네이버 토큰을 확인하고 내준 세션으로 로그인 */
    fun signInWith(session: SessionDto) {
        store.current = session.toSession(clock())
    }

    fun signOut() {
        store.current = null
    }
}
