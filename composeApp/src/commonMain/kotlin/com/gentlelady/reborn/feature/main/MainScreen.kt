// com/gentlelady/reborn/feature/main/MainScreen.kt
package com.gentlelady.reborn.feature.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gentlelady.reborn.core.designsystem.navigation.BottomNavigationBar
import com.gentlelady.reborn.feature.home.HomeScreen
import com.gentlelady.reborn.feature.management.managementNavGraph
import com.gentlelady.reborn.feature.memorial.memorialNavGraph // 👈 memorialNavGraph 임포트
import com.gentlelady.reborn.feature.message.MessageScreen
import com.gentlelady.reborn.feature.myprofile.myProfileNavGraph
import com.gentlelady.reborn.feature.search.searchNavGraph
import com.gentlelady.reborn.feature.wreath_purchase.wreathNavGraph
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.management.profile_edit.presentation.PaymentHistoryIntent
import com.gentlelady.reborn.management.profile_edit.presentation.PaymentHistoryState
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedIntent
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedState
import com.gentlelady.reborn.message.presentation.MessageIntent
import com.gentlelady.reborn.message.presentation.MessageState
import com.gentlelady.reborn.myprofile.presentation.MyProfileIntent
import com.gentlelady.reborn.myprofile.presentation.MyProfileState
import com.gentlelady.reborn.search.presentation.SearchIntent
import com.gentlelady.reborn.search.presentation.SearchState

@Composable
fun MainScreen(
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
    onPaymentHistoryIntent: (PaymentHistoryIntent) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var isMemorialWritingHistory by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            // 💡 바텀바를 항상 노출할 라우트 목록 ("memorial/me" 추가). 단, 히스토리 작성 화면에서는 숨긴다.
            val mainRoutes = listOf(
                "home", "search", "message", "profile", "memorial/me", "wreath/checkout/{tier}", "wreath/message/{tier}",
                "management/scheduled_feed", "management/profile_edit", "management/profile_edit/basic_info", "management/profile_edit/payment"
            )
            if (currentRoute in mainRoutes && !isMemorialWritingHistory) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("home") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. 홈 화면 슬롯
            composable("home") {
                HomeScreen(
                    state = homeState,
                    onIntent = onHomeIntent
                )
            }

            // 2. 검색 화면 그래프
            searchNavGraph(
                state = searchState,
                onIntent = onSearchIntent
            )

            // 3. 메시지 화면 슬롯
            composable("message") {
                MessageScreen(
                    state = messageState,
                    onIntent = onMessageIntent
                )
            }

            // 4. 프로필 화면 서브 그래프
            myProfileNavGraph(
                state = myProfileState,
                onIntent = { intent ->
                    when (intent) {
                        // 💡 토글 클릭 시 서브 navController를 통해 "memorial/me"로 전환 (바텀바 유지가능)
                        is MyProfileIntent.ClickToggleMemorialMode -> {
                            navController.navigate("memorial/me")
                        }
                        // 💡 관리 그리드의 메뉴 클릭을 해당 관리 서브 화면 라우트로 연결
                        is MyProfileIntent.ClickManagementMenu -> {
                            when (intent.menuId) {
                                "scheduled_feed" -> navController.navigate("management/scheduled_feed")
                                "edit_profile" -> navController.navigate("management/profile_edit")
                                else -> onMyProfileIntent(intent)
                            }
                        }
                        else -> onMyProfileIntent(intent)
                    }
                }
            )

            // 4-1. 🆕 "관리" 메뉴 하위 화면들의 서브 그래프
            managementNavGraph(
                navController = navController,
                myProfileState = myProfileState,
                scheduledFeedState = scheduledFeedState,
                onScheduledFeedIntent = onScheduledFeedIntent,
                paymentHistoryState = paymentHistoryState,
                onPaymentHistoryIntent = onPaymentHistoryIntent
            )

            // 5. 🆕 내 서브 NavHost 내부에 memorialNavGraph 추가 (바텀바 내부에서 렌더링됨)
            memorialNavGraph(
                navController = navController,
                onWritingHistoryChange = { isMemorialWritingHistory = it }
            )

            // 6. 🆕 화환 구매 서브 그래프 (독립 라우트, memorial/me와 별개로 바텀바 자동 숨김)
            wreathNavGraph(navController = navController)
        }
    }
}