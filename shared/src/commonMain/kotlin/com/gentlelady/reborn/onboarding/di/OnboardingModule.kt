package com.gentlelady.reborn.onboarding.di

import com.gentlelady.reborn.onboarding.presentation.OnboardingViewModel
import org.koin.dsl.module

val onboardingModule = module {
    factory { OnboardingViewModel() }
}
