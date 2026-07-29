package com.gentlelady.reborn.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.core.theme.RebornBackground
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornSurface
import com.gentlelady.reborn.feature.onboarding.components.OnboardingAlertMockup
import com.gentlelady.reborn.feature.onboarding.components.OnboardingFeedMockup
import com.gentlelady.reborn.feature.onboarding.components.OnboardingFlowerMockup
import com.gentlelady.reborn.feature.onboarding.components.OnboardingMemorialCardMockup
import com.gentlelady.reborn.feature.onboarding.components.OnboardingPageContent
import com.gentlelady.reborn.feature.onboarding.components.OnboardingPageIndicator
import com.gentlelady.reborn.feature.onboarding.components.OnboardingScheduleMockup
import com.gentlelady.reborn.onboarding.domain.model.OnboardingPage
import com.gentlelady.reborn.onboarding.domain.model.OnboardingPageType
import com.gentlelady.reborn.onboarding.presentation.OnboardingIntent
import com.gentlelady.reborn.onboarding.presentation.OnboardingState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onIntent: (OnboardingIntent) -> Unit = {}
) {
    val pagerState = rememberPagerState(
        initialPage = state.currentPage,
        pageCount = { state.pages.size }
    )

    // 스와이프로 페이지가 바뀌면 State에도 반영
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            onIntent(OnboardingIntent.PageChanged(pagerState.currentPage))
        }
    }

    // 버튼 클릭으로 State의 currentPage가 바뀌면 Pager도 따라 이동
    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    Scaffold(
        containerColor = RebornBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RebornSurface)
                .padding(padding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val currentPage = state.pages[page]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    OnboardingPageContent(
                        title = currentPage.title,
                        description = currentPage.description
                    ) {
                        OnboardingMockup(type = currentPage.type)
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnboardingPageIndicator(
                    pageCount = state.pages.size,
                    currentPage = state.currentPage
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onIntent(OnboardingIntent.NextClicked) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RebornDeepBlue),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text(text = "다음 →")
                }
            }
        }
    }
}

@Composable
private fun OnboardingMockup(type: OnboardingPageType) {
    when (type) {
        OnboardingPageType.FLOWER_INTRO -> OnboardingFlowerMockup()
        OnboardingPageType.MEMORIAL_CARD -> OnboardingMemorialCardMockup()
        OnboardingPageType.ALERT_CARD -> OnboardingAlertMockup()
        OnboardingPageType.FEED_CARD -> OnboardingFeedMockup()
        OnboardingPageType.SCHEDULE_CARD -> OnboardingScheduleMockup()
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    val mockPages = listOf(
        OnboardingPage(
            id = "preview_1",
            type = OnboardingPageType.MEMORIAL_CARD,
            title = "함께했던 추억을 모아 완성하는\n단 하나의 공간.",
            description = "갤러리에 잠든 사진과 지인들의 방명록\n을 모아 고인의 메모리얼 페이지를 완성\n하세요."
        ),
        OnboardingPage(
            id = "preview_2",
            type = OnboardingPageType.SCHEDULE_CARD,
            title = "시들지 않는 화환과 알림으로,\n계속되는 마음.",
            description = "특별한 날 알림을 받고, 버려지지 않는\n온라인 화환으로 영원한 위로를 전하세요."
        )
    )

    MaterialTheme {
        OnboardingScreen(state = OnboardingState(pages = mockPages, currentPage = 0))
    }
}
