package com.gentlelady.reborn.feedwrite.di

import com.gentlelady.reborn.feedwrite.presentation.FeedWriteViewModel
import org.koin.dsl.module

val feedWriteModule = module {
    factory { FeedWriteViewModel() }
}
