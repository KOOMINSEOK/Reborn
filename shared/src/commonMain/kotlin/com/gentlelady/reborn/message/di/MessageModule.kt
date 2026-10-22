package com.gentlelady.reborn.di

import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.message.data.ChatRepository
import com.gentlelady.reborn.message.data.ChatRepositoryImpl
import com.gentlelady.reborn.message.data.MyGuestbookRepository
import com.gentlelady.reborn.message.data.MyGuestbookRepositoryImpl
import com.gentlelady.reborn.message.presentation.ChatViewModel
import com.gentlelady.reborn.message.presentation.MessageViewModel
import com.gentlelady.reborn.message.presentation.UserSearchViewModel
import org.koin.dsl.module

val messageModule = module {
    // 람다 식 내부에서 생성자를 명시적으로 호출하여 Koin이 타입을 java.lang.Object로 오인하는 현상을 원천 차단합니다.
    single<MyGuestbookRepository> { MyGuestbookRepositoryImpl(get()) }
    single<ChatRepository> { ChatRepositoryImpl(get()) }
    factory { MessageViewModel(get(), get(), CurrentUserIdUseCase(get())) }
    factory { ChatViewModel(get(), CurrentUserIdUseCase(get())) }
    factory { UserSearchViewModel(get(), CurrentUserIdUseCase(get())) }
}
