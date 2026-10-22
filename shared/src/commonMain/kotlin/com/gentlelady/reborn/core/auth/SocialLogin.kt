package com.gentlelady.reborn.core.auth

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/** 소셜 로그인 창을 띄우는 일 (플랫폼마다 — 안드로이드: 브라우저·네이버 SDK). 없는 플랫폼은 등록 안 함. */
interface SocialLoginLauncher {
    /** 로그인 페이지를 브라우저로 연다. 끝나면 [OAuthRedirects] 로 앱에 돌아온다 */
    fun openBrowser(url: String)

    /**
     * 네이버 로그인 → 네이버 access token. 사용자가 취소하면 null, 그 밖의 실패는 예외.
     * [otherAccount] 면 이 기기에 로그인된 네이버 계정을 쓰지 않고 아이디·비밀번호부터 다시 (네이버엔 계정 선택 화면이 없다)
     */
    suspend fun naverAccessToken(otherAccount: Boolean): String?
}

/** 브라우저 로그인 뒤 앱으로 돌아온 주소 ([OAUTH_REDIRECT]?code=… 또는 ?error=…). 플랫폼이 받아 넣는다 */
object OAuthRedirects {
    private val _uris = MutableSharedFlow<String>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val uris: SharedFlow<String> = _uris

    fun deliver(uri: String) {
        _uris.tryEmit(uri)
    }
}

/** Supabase 대시보드 Authentication → URL Configuration → Redirect URLs 에도 같은 값이 있어야 한다 */
const val OAUTH_REDIRECT = "com.gentlelady.reborn://login-callback"
