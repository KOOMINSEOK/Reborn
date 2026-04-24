package com.gentlelady.reborn.di

import com.gentlelady.reborn.home.di.homeModule
import com.gentlelady.reborn.todo.di.todoModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

private var isKoinStarted = false

fun initKoin(appDeclaration: KoinApplication.() -> Unit = {}) {
    if (isKoinStarted) return

    startKoin {
        appDeclaration()
        modules(
            todoModule,
            homeModule
        )
    }
    isKoinStarted = true
}
