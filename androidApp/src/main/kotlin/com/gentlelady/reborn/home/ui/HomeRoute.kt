package com.gentlelady.reborn.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import com.gentlelady.reborn.App
import com.gentlelady.reborn.auth.presentation.AuthViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gentlelady.reborn.feature.signup.SignupFlow
import com.gentlelady.reborn.signup.SignupGate
import com.gentlelady.reborn.signup.SignupIntent
import com.gentlelady.reborn.signup.SignupStep
import com.gentlelady.reborn.signup.SignupViewModel
import com.gentlelady.reborn.auth.presentation.LoginIntent
import com.gentlelady.reborn.feature.auth.LoginScreen
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeViewModel
import com.gentlelady.reborn.search.presentation.SearchViewModel
import com.gentlelady.reborn.message.presentation.MessageViewModel
import com.gentlelady.reborn.management.app_settings.customer_support.faq.presentation.FaqViewModel
import com.gentlelady.reborn.management.app_settings.notification_settings.presentation.NotificationSettingsViewModel
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
    // 첫 화면 게이트:
    // - 로그인 안 함 → 가입 시작 화면 (거기서 "로그인" → 이메일 로그인 화면, 로그인 화면의 "둘러보기"면 mock 으로 앱 사용)
    // - 세션은 있는데 가입이 덜 끝남(아이디 안 만듦) → 남은 가입 단계부터
    val authViewModel: AuthViewModel = koinViewModel()
    val loginState by authViewModel.state.collectAsState()
    val signupViewModel: SignupViewModel = koinViewModel()
    val signupState by signupViewModel.state.collectAsState()
    var skippedLogin by rememberSaveable { mutableStateOf(false) }

    if (!(skippedLogin && !loginState.loggedIn)) {
        when (signupState.gate) {
            // 저장된 세션의 가입 상태를 묻는 동안 — 잠깐 흰 화면 (앱이 먼저 보였다가 가입 화면으로 튀지 않게)
            SignupGate.CHECKING -> {
                Box(Modifier.fillMaxSize().background(Color.White))
                return
            }
            SignupGate.SIGNUP -> {
                if (signupState.showLogin) {
                    BackHandler { signupViewModel.onIntent(SignupIntent.CloseLogin) }
                    LoginScreen(
                        state = loginState,
                        onIntent = authViewModel::handleIntent,
                        onSkip = { skippedLogin = true },
                    )
                } else {
                    BackHandler(enabled = signupState.step != SignupStep.START) { signupViewModel.onIntent(SignupIntent.Back) }
                    SignupFlow(signupState, signupViewModel::onIntent)
                }
                return
            }
            SignupGate.DONE -> Unit
        }
    }

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
    val notificationSettingsViewModel: NotificationSettingsViewModel = koinViewModel() // 🆕 관리 > 알림 설정 뇌 추가 주입
    val faqViewModel: FaqViewModel = koinViewModel() // 🆕 관리 > FAQ 뇌 추가 주입

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
    val notificationSettingsState by notificationSettingsViewModel.state.collectAsState() // 🆕 알림 설정 상태 수집 추가
    val faqState by faqViewModel.state.collectAsState() // 🆕 FAQ 상태 수집 추가

    // 3. 초기 데이터 로드 명령 (Intent) - 최초 화면 진입 시 트리거용
    LaunchedEffect(Unit) {
        homeViewModel.handleIntent(HomeIntent.LoadFeed)
        // messageViewModel.handleIntent(MessageIntent.LoadMessageTabs)

        // 🆕 최초 진입 시 가상 데이터 소스(MockDataSource)로부터 프로필 정보를 당겨오도록 명령 트리거
        myProfileViewModel.handleIntent(MyProfileIntent.LoadProfile)
    }

    // 설정 > 차단한 계정에서 풀면 그 사람 글이 다시 보이게 피드를 다시 받는다
    val unblockedToken = blockedAccountsState.unblockedToken
    LaunchedEffect(unblockedToken) {
        if (unblockedToken > 0) homeViewModel.handleIntent(HomeIntent.LoadFeed)
    }

    // 로그인 상태가 바뀌면 피드·내 프로필을 다시 불러온다 (mock ↔ 서버 데이터 전환, 다른 계정 사진이 남지 않게).
    LaunchedEffect(loginState.loggedIn) {
        homeViewModel.handleIntent(HomeIntent.LoadFeed)
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
        onBlockedAccountsIntent = { intent -> blockedAccountsViewModel.handleIntent(intent) },
        notificationSettingsState = notificationSettingsState,
        onNotificationSettingsIntent = { intent -> notificationSettingsViewModel.handleIntent(intent) },
        faqState = faqState,
        onFaqIntent = { intent -> faqViewModel.handleIntent(intent) },
        onSignOut = {
            authViewModel.handleIntent(LoginIntent.SignOut)
            skippedLogin = false // 로그인 화면으로 (둘러보기 상태였어도)
        }
    )
}
