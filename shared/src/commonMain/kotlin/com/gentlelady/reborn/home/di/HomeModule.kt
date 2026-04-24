package com.gentlelady.reborn.home.di

import com.gentlelady.reborn.home.data.repository.HomeRepositoryImpl
import com.gentlelady.reborn.home.domain.repository.HomeRepository
import com.gentlelady.reborn.home.domain.usecase.GetHomeFeedUseCase
import com.gentlelady.reborn.home.presentation.home.HomeViewModel
import org.koin.dsl.module

val homeModule = module {
    // Data
    single<HomeRepository> { HomeRepositoryImpl() }

    // Domain
    factory { GetHomeFeedUseCase(get()) }

    // Presentation
    factory { HomeViewModel(get()) }
}