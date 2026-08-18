package com.gentlelady.reborn.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.gentlelady.reborn.App
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeViewModel
import com.gentlelady.reborn.search.presentation.SearchViewModel
import com.gentlelady.reborn.message.presentation.MessageViewModel
import com.gentlelady.reborn.management.archive.presentation.ArchiveViewModel
import com.gentlelady.reborn.management.profile_edit.presentation.PaymentHistoryViewModel
import com.gentlelady.reborn.management.saved.presentation.SavedViewModel
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedViewModel
import com.gentlelady.reborn.management.security.account_visibility.presentation.AccountVisibilityViewModel
import com.gentlelady.reborn.management.security.blocked_accounts.presentation.BlockedAccountsViewModel
import com.gentlelady.reborn.management.security.change_password.presentation.ChangePasswordViewModel
import com.gentlelady.reborn.management.security.device_management.presentation.DeviceManagementViewModel
import com.gentlelady.reborn.myprofile.presentation.MyProfileIntent  // 🆕 프로필 인텐트 임포트 추가
import com.gentlelady.reborn.myprofile.presentation.MyProfileViewModel // 🆕 프로필 ViewModel 임포트 추가
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute() {
    // 1. Koin을 통해 Shared 모듈의 네 개의 뇌(ViewModel)를 모두 주입받음
    val homeViewModel: HomeViewModel = koinViewModel()
    val searchViewModel: SearchViewModel = koinViewModel()
    val messageViewModel: MessageViewModel = koinViewModel()
    val myProfileViewModel: MyProfileViewModel = koinViewModel() // 🆕 프로필 뇌 추가 주입
    val scheduledFeedViewModel: ScheduledFeedViewModel = koinViewModel() // 🆕 관리 > 예약된 피드 뇌 추가 주입
    val paymentHistoryViewModel: PaymentHistoryViewModel = koinViewModel() // 🆕 관리 > 결제 관리 뇌 추가 주입
    val archiveViewModel: ArchiveViewModel = koinViewModel() // 🆕 관리 > 보관함 뇌 추가 주입
    val savedViewModel: SavedViewModel = koinViewModel() // 🆕 관리 > 저장(스크랩) 뇌 추가 주입
    val accountVisibilityViewModel: AccountVisibilityViewModel = koinViewModel() // 🆕 관리 > 계정 공개 범위 뇌 추가 주입
    val deviceManagementViewModel: DeviceManagementViewModel = koinViewModel() // 🆕 관리 > 로그인 기기 관리 뇌 추가 주입
    val changePasswordViewModel: ChangePasswordViewModel = koinViewModel() // 🆕 관리 > 비밀번호 변경 뇌 추가 주입
    val blockedAccountsViewModel: BlockedAccountsViewModel = koinViewModel() // 🆕 관리 > 차단 계정 뇌 추가 주입

    // 2. 각 영역의 독립적인 MVI State 관찰(수집) 및 단일 진실 공급원(SSOT) 수립
    val homeState by homeViewModel.state.collectAsState()
    val searchState by searchViewModel.state.collectAsState()
    val messageState by messageViewModel.state.collectAsState()
    val myProfileState by myProfileViewModel.state.collectAsState() // 🆕 프로필 상태 수집 추가
    val scheduledFeedState by scheduledFeedViewModel.state.collectAsState() // 🆕 예약된 피드 상태 수집 추가
    val paymentHistoryState by paymentHistoryViewModel.state.collectAsState() // 🆕 결제 관리 상태 수집 추가
    val archiveState by archiveViewModel.state.collectAsState() // 🆕 보관함 상태 수집 추가
    val savedState by savedViewModel.state.collectAsState() // 🆕 저장(스크랩) 상태 수집 추가
    val accountVisibilityState by accountVisibilityViewModel.state.collectAsState() // 🆕 계정 공개 범위 상태 수집 추가
    val deviceManagementState by deviceManagementViewModel.state.collectAsState() // 🆕 로그인 기기 관리 상태 수집 추가
    val changePasswordState by changePasswordViewModel.state.collectAsState() // 🆕 비밀번호 변경 상태 수집 추가
    val blockedAccountsState by blockedAccountsViewModel.state.collectAsState() // 🆕 차단 계정 상태 수집 추가

    // 3. 초기 데이터 로드 명령 (Intent) - 최초 화면 진입 시 트리거용
    LaunchedEffect(Unit) {
        homeViewModel.handleIntent(HomeIntent.LoadFeed)
        // messageViewModel.handleIntent(MessageIntent.LoadMessageTabs)

        // 🆕 최초 진입 시 가상 데이터 소스(MockDataSource)로부터 프로필 정보를 당겨오도록 명령 트리거
        myProfileViewModel.handleIntent(MyProfileIntent.LoadProfile)
    }

    // 4. composeApp의 공통 진입점인 App() 호출 및 완벽한 최상위 데이터 파이프라인 완결
    App(
        homeState = homeState,
        onHomeIntent = { intent -> homeViewModel.handleIntent(intent) },
        searchState = searchState,
        onSearchIntent = { intent -> searchViewModel.dispatch(intent) },
        messageState = messageState,
        onMessageIntent = { intent -> messageViewModel.handleIntent(intent) },
        myProfileState = myProfileState, // 🆕 수집된 프로필 상태 파라미터 하향 주입
        onMyProfileIntent = { intent -> myProfileViewModel.handleIntent(intent) }, // 🆕 프로필 인텐트 핸들러 바인딩 완료
        scheduledFeedState = scheduledFeedState,
        onScheduledFeedIntent = { intent -> scheduledFeedViewModel.handleIntent(intent) },
        paymentHistoryState = paymentHistoryState,
        onPaymentHistoryIntent = { intent -> paymentHistoryViewModel.handleIntent(intent) },
        archiveState = archiveState,
        onArchiveIntent = { intent -> archiveViewModel.handleIntent(intent) },
        savedState = savedState,
        onSavedIntent = { intent -> savedViewModel.handleIntent(intent) },
        accountVisibilityState = accountVisibilityState,
        onAccountVisibilityIntent = { intent -> accountVisibilityViewModel.handleIntent(intent) },
        deviceManagementState = deviceManagementState,
        onDeviceManagementIntent = { intent -> deviceManagementViewModel.handleIntent(intent) },
        changePasswordState = changePasswordState,
        onChangePasswordIntent = { intent -> changePasswordViewModel.handleIntent(intent) },
        blockedAccountsState = blockedAccountsState,
        onBlockedAccountsIntent = { intent -> blockedAccountsViewModel.handleIntent(intent) }
    )
}
