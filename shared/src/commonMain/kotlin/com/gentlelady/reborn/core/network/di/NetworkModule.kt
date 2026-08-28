package com.gentlelady.reborn.core.network.di

import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.createRebornHttpClient
import org.koin.dsl.module

// TokenProvider 는 authModule 이 제공한다 (SessionTokenProvider).
val networkModule = module {
    single { createRebornHttpClient(get()) }
    single { RebornApi(get()) }
}
