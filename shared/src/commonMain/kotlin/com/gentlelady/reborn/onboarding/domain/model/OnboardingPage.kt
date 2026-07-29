package com.gentlelady.reborn.onboarding.domain.model

data class OnboardingPage(
    val id: String,
    val type: OnboardingPageType,
    val title: String,
    val description: String
)

enum class OnboardingPageType {
    FLOWER_INTRO,
    MEMORIAL_CARD,
    ALERT_CARD,
    FEED_CARD,
    SCHEDULE_CARD
}
