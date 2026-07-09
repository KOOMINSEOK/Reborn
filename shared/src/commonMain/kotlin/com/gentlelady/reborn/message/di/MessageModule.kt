package com.gentlelady.reborn.di

import com.gentlelady.reborn.message.presentation.MessageViewModel
import org.koin.dsl.module

val messageModule = module {
    // 람다 식 내부에서 생성자를 명시적으로 호출하여 Koin이 타입을 java.lang.Object로 오인하는 현상을 원천 차단합니다.
    factory { MessageViewModel() }
}