package com.gentlelady.reborn.di

import com.gentlelady.reborn.home.di.homeModule
import com.gentlelady.reborn.onboarding.di.onboardingModule
import com.gentlelady.reborn.profile.domain.di.profileModule
import com.gentlelady.reborn.search.di.searchModule // 1. 방금 만든 searchModule 임포트
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
            homeModule,
            searchModule,
            messageModule,
            profileModule,// 2. 여기에 검색 모듈을 결합해 줍니다.
            onboardingModule
        )
    }
    isKoinStarted = true
}