package com.gentlelady.reborn.onboarding.presentation

import com.gentlelady.reborn.onboarding.domain.model.OnboardingPage

data class OnboardingState(
    val pages: List<OnboardingPage> = emptyList(),
    val currentPage: Int = 0,
    val isCompleted: Boolean = false
)

sealed interface OnboardingIntent {
    data class PageChanged(val page: Int) : OnboardingIntent
    object NextClicked : OnboardingIntent
}
