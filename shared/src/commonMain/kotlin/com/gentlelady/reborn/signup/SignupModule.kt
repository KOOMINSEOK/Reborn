package com.gentlelady.reborn.signup

import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import org.koin.dsl.module

val signupModule = module {
    single<SignupRepository> { SignupRepositoryImpl(get(), get(), get()) }
    factory { SignupViewModel(get(), CurrentUserIdUseCase(get())) }
}
