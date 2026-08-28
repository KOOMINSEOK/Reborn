package com.gentlelady.reborn.core.image

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory

/** 원격 이미지(서버 URL)를 로드하는 Coil ImageLoader. App() 에서 SingletonImageLoader 로 등록한다. */
fun newRebornImageLoader(context: PlatformContext): ImageLoader =
    ImageLoader.Builder(context)
        .components { add(KtorNetworkFetcherFactory()) }
        .build()
