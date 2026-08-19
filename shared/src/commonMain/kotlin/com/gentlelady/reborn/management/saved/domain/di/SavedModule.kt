package com.gentlelady.reborn.management.saved.domain.di

import com.gentlelady.reborn.management.saved.presentation.SavedViewModel
import org.koin.dsl.module

val savedModule = module {
    factory { SavedViewModel() }
}
