package com.gentlelady.reborn.management.profile_edit.domain.di

import com.gentlelady.reborn.management.profile_edit.presentation.PaymentHistoryViewModel
import org.koin.dsl.module

val paymentHistoryModule = module {
    factory { PaymentHistoryViewModel() }
}
