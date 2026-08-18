package com.gentlelady.reborn.management.security.domain.di

import com.gentlelady.reborn.management.security.account_visibility.presentation.AccountVisibilityViewModel
import org.koin.dsl.module

val securityModule = module {
    factory { AccountVisibilityViewModel() }
}
