package com.gentlelady.reborn.search.di

import com.gentlelady.reborn.search.presentation.SearchViewModel
import org.koin.dsl.module

val searchModule = module {
    // 1. Data Layer (향후 Repository 구현체 등록부)
    // single<SearchRepository> { SearchRepositoryImpl(get()) }

    // 2. Domain Layer (향후 검색 UseCase 등록부)
    // factory { ExecuteSearchUseCase(get()) }

    // 3. Presentation Layer (UI의 두뇌 역할을 하는 ViewModel)
    // 지금은 인자 없이(get 없이) 프로토타입용으로 우선 등록합니다.
    factory { SearchViewModel() }
}