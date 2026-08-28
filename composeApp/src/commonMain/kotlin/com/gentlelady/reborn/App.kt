package com.gentlelady.reborn

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import coil3.SingletonImageLoader
import com.gentlelady.reborn.core.image.newRebornImageLoader
import com.gentlelady.reborn.feature.main.mainNavGraph
import com.gentlelady.reborn.feature.memorial.memorialNavGraph
import com.gentlelady.reborn.feature.memorial_swipe.memorialSwipeNavGraph
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.message.presentation.MessageIntent
import com.gentlelady.reborn.message.presentation.MessageState
import com.gentlelady.reborn.management.app_settings.customer_support.faq.presentation.FaqIntent
import com.gentlelady.reborn.management.app_settings.customer_support.faq.presentation.FaqState
import com.gentlelady.reborn.management.app_settings.notification_settings.presentation.NotificationSettingsIntent
import com.gentlelady.reborn.management.app_settings.notification_settings.presentation.NotificationSettingsState
import com.gentlelady.reborn.management.archive.presentation.ArchiveIntent
import com.gentlelady.reborn.management.archive.presentation.ArchiveState
import com.gentlelady.reborn.management.profile_edit.presentation.PaymentHistoryIntent
import com.gentlelady.reborn.management.profile_edit.presentation.PaymentHistoryState
import com.gentlelady.reborn.management.saved.presentation.SavedIntent
import com.gentlelady.reborn.management.saved.presentation.SavedState
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedIntent
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedState
import com.gentlelady.reborn.management.security.account_visibility.presentation.AccountVisibilityIntent
import com.gentlelady.reborn.management.security.account_visibility.presentation.AccountVisibilityState
import com.gentlelady.reborn.management.security.blocked_accounts.presentation.BlockedAccountsIntent
import com.gentlelady.reborn.management.security.blocked_accounts.presentation.BlockedAccountsState
import com.gentlelady.reborn.management.security.change_password.presentation.ChangePasswordIntent
import com.gentlelady.reborn.management.security.change_password.presentation.ChangePasswordState
import com.gentlelady.reborn.management.security.device_management.presentation.DeviceManagementIntent
import com.gentlelady.reborn.management.security.device_management.presentation.DeviceManagementState
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
    onArchiveIntent: (ArchiveIntent) -> Unit,
    savedState: SavedState,
    onSavedIntent: (SavedIntent) -> Unit,
    accountVisibilityState: AccountVisibilityState,
    onAccountVisibilityIntent: (AccountVisibilityIntent) -> Unit,
    deviceManagementState: DeviceManagementState,
    onDeviceManagementIntent: (DeviceManagementIntent) -> Unit,
    changePasswordState: ChangePasswordState,
    onChangePasswordIntent: (ChangePasswordIntent) -> Unit,
    blockedAccountsState: BlockedAccountsState,
    onBlockedAccountsIntent: (BlockedAccountsIntent) -> Unit,
    notificationSettingsState: NotificationSettingsState,
    onNotificationSettingsIntent: (NotificationSettingsIntent) -> Unit,
    faqState: FaqState,
    onFaqIntent: (FaqIntent) -> Unit
) {
    SingletonImageLoader.setSafe { context -> newRebornImageLoader(context) }

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
                onArchiveIntent = onArchiveIntent,
                savedState = savedState,
                onSavedIntent = onSavedIntent,
                accountVisibilityState = accountVisibilityState,
                onAccountVisibilityIntent = onAccountVisibilityIntent,
                deviceManagementState = deviceManagementState,
                onDeviceManagementIntent = onDeviceManagementIntent,
                changePasswordState = changePasswordState,
                onChangePasswordIntent = onChangePasswordIntent,
                blockedAccountsState = blockedAccountsState,
                onBlockedAccountsIntent = onBlockedAccountsIntent,
                notificationSettingsState = notificationSettingsState,
                onNotificationSettingsIntent = onNotificationSettingsIntent,
                faqState = faqState,
                onFaqIntent = onFaqIntent
            )

            // 2. 탭바를 숨겨야 하는 몰입형 스와이프 화면만 최상위로 유지
            memorialSwipeNavGraph(
                navController = rootNavController
            )
        }
    }
}