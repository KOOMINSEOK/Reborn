package com.gentlelady.reborn.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

/**
 * 세션을 기기에 남기는 곳. 플랫폼이 구현한다 (Android: Keystore 로 암호화한 파일).
 * 문자열(JSON)만 다룬다 — 직렬화는 [SessionStore] 가, 보관·암호화는 플랫폼이.
 */
interface SessionStorage {
    fun read(): String?
    fun write(value: String)
    fun clear()

    /** 저장소가 없는 플랫폼(iOS·웹·테스트): 메모리에만 — 앱을 끄면 다시 로그인. */
    object None : SessionStorage {
        override fun read(): String? = null
        override fun write(value: String) = Unit
        override fun clear() = Unit
    }
}

/**
 * 현재 로그인 세션. 바뀔 때마다 [storage] 에 저장하고, 만들어질 때 저장된 세션을 불러온다 → 앱을 다시 켜도 로그인 유지.
 * 로그아웃(또는 토큰 갱신 실패)으로 null 이 되면 저장된 세션도 지운다.
 */
class SessionStore(
    private val storage: SessionStorage = SessionStorage.None,
) {
    private val _session = MutableStateFlow(restore())
    val session: StateFlow<Session?> = _session.asStateFlow()

    var current: Session?
        get() = _session.value
        set(value) {
            _session.value = value
            if (value == null) storage.clear() else storage.write(json.encodeToString(Session.serializer(), value))
        }

    // 깨졌거나 못 읽는 값(앱 재설치로 암호화 키가 바뀜 등)은 버리고 로그인 화면부터
    private fun restore(): Session? = storage.read()?.let { raw ->
        runCatching { json.decodeFromString(Session.serializer(), raw) }
            .getOrElse {
                storage.clear()
                null
            }
    }
}

private val json = Json { ignoreUnknownKeys = true }
