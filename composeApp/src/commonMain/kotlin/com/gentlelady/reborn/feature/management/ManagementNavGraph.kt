// feature/management/ManagementNavGraph.kt
package com.gentlelady.reborn.feature.management

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.feature.management.archive.ArchiveScreen
import com.gentlelady.reborn.feature.management.profile_edit.BasicInfoManagementScreen
import com.gentlelady.reborn.feature.management.profile_edit.PaymentManagementScreen
import com.gentlelady.reborn.feature.management.profile_edit.ProfileEditScreen
import com.gentlelady.reborn.feature.management.saved.SavedScreen
import com.gentlelady.reborn.feature.management.scheduled_feed.ScheduledFeedScreen
import com.gentlelady.reborn.feature.management.security.SecurityScreen
import com.gentlelady.reborn.feature.management.security.account_visibility.AccountVisibilityScreen
import com.gentlelady.reborn.feature.management.security.blocked_accounts.BlockedAccountsScreen
import com.gentlelady.reborn.feature.management.security.change_password.ChangePasswordScreen
import com.gentlelady.reborn.feature.management.security.device_management.DeviceListScreen
import com.gentlelady.reborn.feature.management.security.device_management.DevicePasswordScreen
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
import com.gentlelady.reborn.myprofile.presentation.MyProfileState

/**
 * "관리" 섹션 하위 메뉴(예약된 피드/리마인드, 프로필/결제, 보관, 저장, 공개 범위/보안 등)의 화면들을 모아두는 서브 네비게이션 그래프.
 * MyProfile의 관리 그리드에서 진입하며, 메뉴가 늘어날 때마다 이 파일에 route를 추가한다.
 */
fun NavGraphBuilder.managementNavGraph(
    navController: NavController,
    myProfileState: MyProfileState,
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
    onBlockedAccountsIntent: (BlockedAccountsIntent) -> Unit
) {
    composable("management/scheduled_feed") {
        ScheduledFeedScreen(
            state = scheduledFeedState,
            onIntent = { intent ->
                when (intent) {
                    is ScheduledFeedIntent.ClickBack -> navController.popBackStack()
                    else -> onScheduledFeedIntent(intent)
                }
            }
        )
    }

    composable("management/profile_edit") {
        ProfileEditScreen(
            onBackClick = { navController.popBackStack() },
            onClickBasicInfo = { navController.navigate("management/profile_edit/basic_info") },
            onClickPayment = { navController.navigate("management/profile_edit/payment") }
        )
    }

    composable("management/profile_edit/basic_info") {
        BasicInfoManagementScreen(
            profileState = myProfileState,
            onBackClick = { navController.popBackStack() },
            onClickNickname = { /* TODO: 닉네임 편집 화면 */ },
            onClickProfilePhoto = { /* TODO: 프로필 사진 편집 화면 */ },
            onClickBio = { /* TODO: 한 줄 소개 편집 화면 */ }
        )
    }

    composable("management/profile_edit/payment") {
        PaymentManagementScreen(
            state = paymentHistoryState,
            onIntent = { intent ->
                when (intent) {
                    is PaymentHistoryIntent.ClickBack -> navController.popBackStack()
                    else -> onPaymentHistoryIntent(intent)
                }
            }
        )
    }

    composable("management/archive") {
        ArchiveScreen(
            state = archiveState,
            onIntent = { intent ->
                when (intent) {
                    is ArchiveIntent.ClickBack -> navController.popBackStack()
                    else -> onArchiveIntent(intent)
                }
            }
        )
    }

    composable("management/saved") {
        SavedScreen(
            state = savedState,
            onIntent = { intent ->
                when (intent) {
                    is SavedIntent.ClickBack -> navController.popBackStack()
                    else -> onSavedIntent(intent)
                }
            }
        )
    }

    composable("management/security") {
        SecurityScreen(
            onBackClick = { navController.popBackStack() },
            onClickAccountVisibility = { navController.navigate("management/security/account_visibility") },
            onClickDeviceManagement = { navController.navigate("management/security/devices") },
            onClickChangePassword = { navController.navigate("management/security/change_password") },
            onClickBlockedAccounts = { navController.navigate("management/security/blocked_accounts") }
        )
    }

    composable("management/security/account_visibility") {
        AccountVisibilityScreen(
            state = accountVisibilityState,
            onIntent = { intent ->
                when (intent) {
                    is AccountVisibilityIntent.ClickBack -> navController.popBackStack()
                    else -> onAccountVisibilityIntent(intent)
                }
            }
        )
    }

    composable("management/security/devices") {
        DevicePasswordScreen(
            state = deviceManagementState,
            onIntent = { intent ->
                when (intent) {
                    is DeviceManagementIntent.ClickBack -> navController.popBackStack()
                    else -> onDeviceManagementIntent(intent)
                }
            },
            onVerified = { navController.navigate("management/security/devices/list") }
        )
    }

    composable("management/security/devices/list") {
        DeviceListScreen(
            state = deviceManagementState,
            onIntent = { intent ->
                when (intent) {
                    is DeviceManagementIntent.ClickCloseDeviceList ->
                        navController.popBackStack("management/security", inclusive = false)
                    else -> onDeviceManagementIntent(intent)
                }
            }
        )
    }

    composable("management/security/change_password") {
        ChangePasswordScreen(
            profileState = myProfileState,
            state = changePasswordState,
            onIntent = { intent ->
                when (intent) {
                    is ChangePasswordIntent.ClickBack -> navController.popBackStack()
                    else -> onChangePasswordIntent(intent)
                }
            }
        )
    }

    composable("management/security/blocked_accounts") {
        BlockedAccountsScreen(
            state = blockedAccountsState,
            onIntent = { intent ->
                when (intent) {
                    is BlockedAccountsIntent.ClickBack -> navController.popBackStack()
                    else -> onBlockedAccountsIntent(intent)
                }
            }
        )
    }
}
