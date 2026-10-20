package com.gentlelady.reborn.auth

import com.gentlelady.reborn.core.auth.Session
import com.gentlelady.reborn.core.auth.SessionStorage
import com.gentlelady.reborn.core.auth.SessionStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SessionStoreTest {

    private class MemoryStorage(var value: String? = null) : SessionStorage {
        override fun read(): String? = value
        override fun write(value: String) { this.value = value }
        override fun clear() { value = null }
    }

    private val session = Session("AT", "RT", expiresAtEpochSeconds = 100, userId = "u1")

    @Test
    fun savesOnLoginAndRestoresOnNextLaunch() {
        val storage = MemoryStorage()
        SessionStore(storage).current = session

        assertEquals(session, SessionStore(storage).current) // 앱을 다시 켠 것과 같다
    }

    @Test
    fun logoutClearsStoredSession() {
        val storage = MemoryStorage()
        val store = SessionStore(storage)
        store.current = session
        store.current = null

        assertNull(storage.value)
        assertNull(SessionStore(storage).current)
    }

    @Test
    fun corruptedValueIsDiscarded() {
        val storage = MemoryStorage("not json")
        assertNull(SessionStore(storage).current)
        assertNull(storage.value)
    }
}
