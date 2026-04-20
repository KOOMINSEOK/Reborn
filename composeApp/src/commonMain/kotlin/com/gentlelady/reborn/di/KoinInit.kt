package com.gentlelady.reborn.di

import org.koin.core.context.startKoin

private var isKoinStarted = false

fun initKoin() {
    if (isKoinStarted) return

    startKoin {
        modules(todoModule)
    }
    isKoinStarted = true
}
