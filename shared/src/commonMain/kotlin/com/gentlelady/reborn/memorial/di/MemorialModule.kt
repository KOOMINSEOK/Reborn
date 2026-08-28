package com.gentlelady.reborn.memorial.di

import com.gentlelady.reborn.memorial.data.MemorialRepository
import com.gentlelady.reborn.memorial.data.MemorialRepositoryImpl
import com.gentlelady.reborn.memorial.presentation.MemorialViewModel
import org.koin.dsl.module

val memorialModule = module {
    single<MemorialRepository> { MemorialRepositoryImpl(get()) }
    factory { MemorialViewModel(get()) }
}
