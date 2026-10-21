package com.gentlelady.reborn.myprofile.domain.di

import com.gentlelady.reborn.myprofile.data.MyProfileRepository
import com.gentlelady.reborn.myprofile.data.MyProfileRepositoryImpl
import com.gentlelady.reborn.myprofile.domain.usecase.UpdateAvatarUseCase
import com.gentlelady.reborn.myprofile.domain.usecase.UpdateProfileTextUseCase
import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.myprofile.presentation.MyProfileViewModel
import org.koin.dsl.module

val myProfileModule = module {
    single<MyProfileRepository> { MyProfileRepositoryImpl(get(), get()) }
    factory { UpdateAvatarUseCase(get()) }
    factory { UpdateProfileTextUseCase(get()) }
    factory { MyProfileViewModel(get(), get(), get(), CurrentUserIdUseCase(get())) } // ◀ 이 구문이 DI 컨테이너에 등록되어 있어야 에러가 나지 않습니다.
}
