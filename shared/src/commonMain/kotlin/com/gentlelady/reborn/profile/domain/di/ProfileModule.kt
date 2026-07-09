package com.gentlelady.reborn.profile.domain.di

import com.gentlelady.reborn.profile.presentation.ProfileViewModel
import org.koin.dsl.module

val profileModule = module {
    factory { ProfileViewModel() } // ◀ 이 구문이 DI 컨테이너에 등록되어 있어야 에러가 나지 않습니다.
}