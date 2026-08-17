package com.gentlelady.reborn.management.scheduled_feed.domain.di

import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedViewModel
import org.koin.dsl.module

val scheduledFeedModule = module {
    factory { ScheduledFeedViewModel() }
}
