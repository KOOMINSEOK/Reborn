// com/gentlelady/reborn/feature/main/MainNavGraph.kt
package com.gentlelady.reborn.feature.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
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

fun NavGraphBuilder.mainNavGraph(
    navController: NavController,
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
    composable("main_flow") {
        MainScreen(
            homeState = homeState,
            onHomeIntent = { intent ->
                when (intent) {
                    is HomeIntent.ClickMemorialIcon -> {
                        navController.navigate("memorial_swipe")
                    }
                    else -> onHomeIntent(intent)
                }
            },
            searchState = searchState,
            onSearchIntent = onSearchIntent,
            messageState = messageState,
            onMessageIntent = onMessageIntent,
            myProfileState = myProfileState,
            onMyProfileIntent = onMyProfileIntent, // 💡 MainScreen 내부 핸들러로 위임
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
    }
}