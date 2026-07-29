package com.gentlelady.reborn.data

import com.gentlelady.reborn.onboarding.domain.model.OnboardingPage
import com.gentlelady.reborn.onboarding.domain.model.OnboardingPageType

object OnboardingMockData {
    val pages = listOf(
        OnboardingPage(
            id = "onboarding_1",
            type = OnboardingPageType.FLOWER_INTRO,
            title = "장례식 3일. 소중했던 분을 향한\n추모도 여기서 끝내실 건가요?",
            description = "이제 3일간의 이별을 넘어, 일상 속에서\n그분을 계속 기억하고 추모하세요."
        ),
        OnboardingPage(
            id = "onboarding_2",
            type = OnboardingPageType.MEMORIAL_CARD,
            title = "함께했던 추억을 모아 완성하는\n단 하나의 공간.",
            description = "갤러리에 잠든 사진과 지인들의 방명록\n을 모아 고인의 메모리얼 페이지를 완성\n하세요."
        ),
        OnboardingPage(
            id = "onboarding_3",
            type = OnboardingPageType.ALERT_CARD,
            title = "시들지 않는 화환과 알림으로, 계\n속되는 마음.",
            description = "특별한 날 알림을 받고, 버려지지 않는\n온라인 화환으로 영원한 위로를 전하세\n요."
        ),
        OnboardingPage(
            id = "onboarding_4",
            type = OnboardingPageType.FEED_CARD,
            title = "\"함께 기억할 때, 슬픔은 살아갈\n힘이 됩니다.\"",
            description = "같은 사람을 그리워하는 이들이 모여 일\n상을 나누며 서로를 다독입니다. 단순한\n추모를 넘는 진정한 웰다잉 커뮤니티를\n경험하세요."
        ),
        OnboardingPage(
            id = "onboarding_5",
            type = OnboardingPageType.SCHEDULE_CARD,
            title = "\"가장 나다운 모습으로, 세상 속\n에 남겨지도록.\"",
            description = "나의 소중한 사람들이 훗날 이곳에서 위\n로받을 수 있게, 나의 찬란했던 생애 기록\n과 사후 피드를 미리 기획해 보세요."
        )
    )
}
