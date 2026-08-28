package com.gentlelady.reborn.auth

import com.gentlelady.reborn.core.auth.AuthRepository
import com.gentlelady.reborn.core.auth.Session
import com.gentlelady.reborn.core.auth.SessionStore
import com.gentlelady.reborn.core.auth.SessionTokenProvider
import com.gentlelady.reborn.core.auth.SupabaseAuthApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val sessionJson = """{"access_token":"AT","refresh_token":"RT","expires_in":3600}"""

    private fun authApi(engine: MockEngine) = SupabaseAuthApi(
        HttpClient(engine) { install(ContentNegotiation) { json() } },
    )

    @Test
    fun signInStoresSession() = runTest {
        val store = SessionStore()
        val repo = AuthRepository(
            authApi(MockEngine { respond(sessionJson, HttpStatusCode.OK, jsonHeaders) }),
            store,
        ) { 1_000L }

        assertTrue(repo.signIn("e@x.com", "pw").isSuccess)
        assertEquals("AT", store.current?.accessToken)
        assertEquals(1_000L + 3600, store.current?.expiresAtEpochSeconds)
    }

    @Test
    fun signInFailureLeavesNoSession() = runTest {
        val store = SessionStore()
        val repo = AuthRepository(
            authApi(MockEngine { respond("bad", HttpStatusCode.BadRequest, jsonHeaders) }),
            store,
        ) { 0 }

        assertTrue(repo.signIn("e@x.com", "wrong").isFailure)
        assertNull(store.current)
    }

    @Test
    fun tokenProviderRefreshesExpiringToken() = runTest {
        val store = SessionStore().apply { current = Session("OLD", "RT", expiresAtEpochSeconds = 100) }
        val refreshed = """{"access_token":"NEW","refresh_token":"RT2","expires_in":3600}"""
        val provider = SessionTokenProvider(
            store,
            authApi(MockEngine { respond(refreshed, HttpStatusCode.OK, jsonHeaders) }),
        ) { 1_000L }

        assertEquals("NEW", provider.accessToken())
        assertEquals("NEW", store.current?.accessToken)
    }

    @Test
    fun tokenProviderClearsSessionWhenRefreshFails() = runTest {
        val store = SessionStore().apply { current = Session("OLD", "RT", expiresAtEpochSeconds = 100) }
        val provider = SessionTokenProvider(
            store,
            authApi(MockEngine { respond("nope", HttpStatusCode.Unauthorized, jsonHeaders) }),
        ) { 1_000L }

        assertNull(provider.accessToken())
        assertNull(store.current)
    }

    @Test
    fun tokenProviderReturnsNullWhenLoggedOut() = runTest {
        val provider = SessionTokenProvider(
            SessionStore(),
            authApi(MockEngine { respond("", HttpStatusCode.OK, jsonHeaders) }),
        ) { 0 }

        assertNull(provider.accessToken())
    }
}
