package com.gentlelady.reborn.core.network.di

import com.gentlelady.reborn.core.network.ImageUploader
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.createRebornHttpClient
import com.gentlelady.reborn.core.network.createUploadHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

// TokenProvider 는 authModule 이 제공한다 (SessionTokenProvider).
val networkModule = module {
    single { createRebornHttpClient(get()) }
    single(named("upload")) { createUploadHttpClient() }
    single { RebornApi(client = get(), uploadClient = get(named("upload"))) }
    single { ImageUploader(get()) }
}
