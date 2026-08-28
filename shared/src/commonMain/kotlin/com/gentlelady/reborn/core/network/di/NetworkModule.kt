package com.gentlelady.reborn.core.network.di

import com.gentlelady.reborn.core.network.NoAuthTokenProvider
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.TokenProvider
import com.gentlelady.reborn.core.network.createRebornHttpClient
import org.koin.dsl.module

val networkModule = module {
    single<TokenProvider> { NoAuthTokenProvider }
    single { createRebornHttpClient(get()) }
    single { RebornApi(get()) }
}
