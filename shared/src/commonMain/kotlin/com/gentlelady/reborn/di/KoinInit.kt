package com.gentlelady.reborn.di

import com.gentlelady.reborn.home.di.homeModule
import com.gentlelady.reborn.onboarding.di.onboardingModule
import com.gentlelady.reborn.management.app_settings.domain.di.appSettingsModule
import com.gentlelady.reborn.management.archive.domain.di.archiveModule
import com.gentlelady.reborn.management.saved.domain.di.savedModule
import com.gentlelady.reborn.management.security.domain.di.securityModule
import com.gentlelady.reborn.management.profile_edit.domain.di.paymentHistoryModule
import com.gentlelady.reborn.management.scheduled_feed.domain.di.scheduledFeedModule
import com.gentlelady.reborn.myprofile.domain.di.myProfileModule
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
            myProfileModule,// 2. 여기에 검색 모듈을 결합해 줍니다.
            scheduledFeedModule,
            paymentHistoryModule,
            archiveModule,
            savedModule,
            securityModule,
            appSettingsModule,
            onboardingModule
        )
    }
    isKoinStarted = true
}