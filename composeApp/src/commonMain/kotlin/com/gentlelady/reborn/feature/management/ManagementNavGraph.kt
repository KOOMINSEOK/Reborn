// feature/management/ManagementNavGraph.kt
package com.gentlelady.reborn.feature.management

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.feature.management.profile_edit.BasicInfoManagementScreen
import com.gentlelady.reborn.feature.management.profile_edit.PaymentManagementScreen
import com.gentlelady.reborn.feature.management.profile_edit.ProfileEditScreen
import com.gentlelady.reborn.feature.management.scheduled_feed.ScheduledFeedScreen
import com.gentlelady.reborn.management.profile_edit.presentation.PaymentHistoryIntent
import com.gentlelady.reborn.management.profile_edit.presentation.PaymentHistoryState
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedIntent
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedState
import com.gentlelady.reborn.myprofile.presentation.MyProfileState

/**
 * "관리" 섹션 하위 메뉴(예약된 피드/리마인드, 프로필/결제, 보관, 저장 등)의 화면들을 모아두는 서브 네비게이션 그래프.
 * MyProfile의 관리 그리드에서 진입하며, 메뉴가 늘어날 때마다 이 파일에 route를 추가한다.
 */
fun NavGraphBuilder.managementNavGraph(
    navController: NavController,
    myProfileState: MyProfileState,
    scheduledFeedState: ScheduledFeedState,
    onScheduledFeedIntent: (ScheduledFeedIntent) -> Unit,
    paymentHistoryState: PaymentHistoryState,
    onPaymentHistoryIntent: (PaymentHistoryIntent) -> Unit
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
}
