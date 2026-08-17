package com.gentlelady.reborn.myprofile.domain.di

import com.gentlelady.reborn.myprofile.presentation.MyProfileViewModel
import org.koin.dsl.module

val myProfileModule = module {
    factory { MyProfileViewModel() } // ◀ 이 구문이 DI 컨테이너에 등록되어 있어야 에러가 나지 않습니다.
}
