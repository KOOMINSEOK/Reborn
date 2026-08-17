package com.gentlelady.reborn.management.archive.domain.di

import com.gentlelady.reborn.management.archive.presentation.ArchiveViewModel
import org.koin.dsl.module

val archiveModule = module {
    factory { ArchiveViewModel() }
}
