package com.gentlelady.reborn.memorial.di

import com.gentlelady.reborn.memorial.data.MemorialRepository
import com.gentlelady.reborn.memorial.data.MemorialRepositoryImpl
import com.gentlelady.reborn.memorial.domain.usecase.CreateGuestbookEntryUseCase
import com.gentlelady.reborn.memorial.domain.usecase.CreateHistoryUseCase
import com.gentlelady.reborn.memorial.presentation.MemorialViewModel
import org.koin.dsl.module

val memorialModule = module {
    single<MemorialRepository> { MemorialRepositoryImpl(get(), get(), get()) }
    factory { CreateHistoryUseCase(get()) }
    factory { CreateGuestbookEntryUseCase(get()) }
    factory { MemorialViewModel(get(), get(), get(), get()) }
}
