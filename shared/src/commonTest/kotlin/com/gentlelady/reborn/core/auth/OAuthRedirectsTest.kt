package com.gentlelady.reborn.core.auth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OAuthRedirectsTest {
    @Test
    fun redirectArrivingBeforeTheScreenIsKeptAndUsedOnce() = runTest {
        OAuthRedirects.deliver("$OAUTH_REDIRECT?code=abc") // 앱이 다시 켜지며 화면보다 먼저 도착
        assertEquals("$OAUTH_REDIRECT?code=abc", OAuthRedirects.uris.first())
        // 한 번 썼으면 다음 화면이 같은 코드를 또 받지 않는다
        assertNull(withTimeoutOrNull(100) { OAuthRedirects.uris.firstOrNull() })
    }
}
