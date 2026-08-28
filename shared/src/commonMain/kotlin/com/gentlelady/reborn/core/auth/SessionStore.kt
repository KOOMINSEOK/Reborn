package com.gentlelady.reborn.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 현재 로그인 세션. 지금은 메모리 저장 — 앱 재시작 시 재로그인.
 * ponytail: 영속화는 multiplatform-settings 도입할 때.
 */
class SessionStore {
    private val _session = MutableStateFlow<Session?>(null)
    val session: StateFlow<Session?> = _session.asStateFlow()

    var current: Session?
        get() = _session.value
        set(value) { _session.value = value }
}
