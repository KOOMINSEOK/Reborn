package com.gentlelady.reborn.todo.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

private var isKoinStarted = false

fun initKoin(appDeclaration: KoinApplication.() -> Unit = {}) {
    if (isKoinStarted) return

    startKoin {
        appDeclaration()
        modules(todoModule)
    }
    isKoinStarted = true
}
