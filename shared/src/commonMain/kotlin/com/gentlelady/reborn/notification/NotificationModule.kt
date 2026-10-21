package com.gentlelady.reborn.notification

import org.koin.dsl.module

val notificationModule = module {
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    factory { GetNotificationsUseCase(get()) }
    factory { GetUnreadNotificationCountUseCase(get()) }
    factory { MarkNotificationsReadUseCase(get()) }
    factory { NotificationsViewModel(get(), get()) }
}
