package com.gentlelady.reborn.search.di

import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.search.data.SearchRepository
import com.gentlelady.reborn.search.data.SearchRepositoryImpl
import com.gentlelady.reborn.search.presentation.SearchViewModel
import org.koin.dsl.module

val searchModule = module {
    single<SearchRepository> { SearchRepositoryImpl(get()) }
    factory { SearchViewModel(get(), CurrentUserIdUseCase(get()), get()) }
}
