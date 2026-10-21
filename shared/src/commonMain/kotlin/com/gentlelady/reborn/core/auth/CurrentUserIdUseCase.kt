package com.gentlelady.reborn.core.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** 지금 로그인한 사람의 id (로그아웃이면 null). 계정이 바뀌면 화면 데이터를 버리고 다시 받는 데 쓴다. */
class CurrentUserIdUseCase(private val store: SessionStore) {
    operator fun invoke(): String? = store.current?.userId

    /** 로그인·로그아웃·다른 계정 로그인 때마다 (같은 사람이면 다시 흘리지 않는다) */
    val changes: Flow<String?> get() = store.session.map { it?.userId }.distinctUntilChanged()
}
