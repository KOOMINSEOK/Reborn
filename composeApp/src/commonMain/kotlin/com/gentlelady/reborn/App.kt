package com.gentlelady.reborn

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gentlelady.reborn.feature.main.mainNavGraph
import com.gentlelady.reborn.feature.memorial.memorialNavGraph
import com.gentlelady.reborn.feature.memorial_swipe.memorialSwipeNavGraph
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.message.presentation.MessageIntent
import com.gentlelady.reborn.message.presentation.MessageState
import com.gentlelady.reborn.management.archive.presentation.ArchiveIntent
import com.gentlelady.reborn.management.archive.presentation.ArchiveState
import com.gentlelady.reborn.management.profile_edit.presentation.PaymentHistoryIntent
import com.gentlelady.reborn.management.profile_edit.presentation.PaymentHistoryState
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedIntent
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedState
import com.gentlelady.reborn.myprofile.presentation.MyProfileIntent
import com.gentlelady.reborn.myprofile.presentation.MyProfileState
import com.gentlelady.reborn.search.presentation.SearchIntent
import com.gentlelady.reborn.search.presentation.SearchState

@Composable
fun App(
    homeState: HomeState,
    onHomeIntent: (HomeIntent) -> Unit,
    searchState: SearchState,
    onSearchIntent: (SearchIntent) -> Unit,
    messageState: MessageState,
    onMessageIntent: (MessageIntent) -> Unit,
    myProfileState: MyProfileState,
    onMyProfileIntent: (MyProfileIntent) -> Unit,
    scheduledFeedState: ScheduledFeedState,
    onScheduledFeedIntent: (ScheduledFeedIntent) -> Unit,
    paymentHistoryState: PaymentHistoryState,
    onPaymentHistoryIntent: (PaymentHistoryIntent) -> Unit,
    archiveState: ArchiveState,
    onArchiveIntent: (ArchiveIntent) -> Unit
) {
    MaterialTheme {
        val rootNavController = rememberNavController()

        // App.kt
        NavHost(
            navController = rootNavController,
            startDestination = "main_flow"
        ) {
            // 1. 메인 기능 그래프 (MainScreen 내부 서브 NavHost에서 memorial/me 로 이동 처리)
            mainNavGraph(
                navController = rootNavController,
                homeState = homeState,
                onHomeIntent = onHomeIntent,
                searchState = searchState,
                onSearchIntent = onSearchIntent,
                messageState = messageState,
                onMessageIntent = onMessageIntent,
                myProfileState = myProfileState,
                onMyProfileIntent = onMyProfileIntent,
                scheduledFeedState = scheduledFeedState,
                onScheduledFeedIntent = onScheduledFeedIntent,
                paymentHistoryState = paymentHistoryState,
                onPaymentHistoryIntent = onPaymentHistoryIntent,
                archiveState = archiveState,
                onArchiveIntent = onArchiveIntent
            )

            // 2. 탭바를 숨겨야 하는 몰입형 스와이프 화면만 최상위로 유지
            memorialSwipeNavGraph(
                navController = rootNavController
            )
        }
    }
}