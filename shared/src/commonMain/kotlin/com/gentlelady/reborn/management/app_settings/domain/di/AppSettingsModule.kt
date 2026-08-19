package com.gentlelady.reborn.management.app_settings.domain.di

import com.gentlelady.reborn.management.app_settings.customer_support.faq.presentation.FaqViewModel
import com.gentlelady.reborn.management.app_settings.notification_settings.presentation.NotificationSettingsViewModel
import org.koin.dsl.module

val appSettingsModule = module {
    factory { NotificationSettingsViewModel() }
    factory { FaqViewModel() }
}
