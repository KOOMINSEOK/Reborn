package com.gentlelady.reborn.core.network

/**
 * 현재 로그인 세션의 Supabase 액세스 토큰. 없으면 null → 인증 필요 호출은 실패하고
 * 레포지토리가 mock 으로 폴백한다.
 */
fun interface TokenProvider {
    suspend fun accessToken(): String?
}

/** 로그인 기능이 붙기 전까지 쓰는 기본 구현. */
object NoAuthTokenProvider : TokenProvider {
    override suspend fun accessToken(): String? = null
}
