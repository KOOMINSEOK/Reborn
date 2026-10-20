package com.gentlelady.reborn.feedwrite.di

import com.gentlelady.reborn.feedwrite.data.repository.FeedWriteRepositoryImpl
import com.gentlelady.reborn.feedwrite.domain.repository.FeedWriteRepository
import com.gentlelady.reborn.feedwrite.domain.usecase.CreatePostUseCase
import com.gentlelady.reborn.feedwrite.presentation.FeedWriteViewModel
import org.koin.dsl.module

val feedWriteModule = module {
    single<FeedWriteRepository> { FeedWriteRepositoryImpl(get(), get()) }
    factory { CreatePostUseCase(get()) }
    factory { FeedWriteViewModel(get()) }
}
