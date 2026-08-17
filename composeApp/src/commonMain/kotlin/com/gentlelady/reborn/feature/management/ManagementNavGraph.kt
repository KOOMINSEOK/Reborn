// feature/management/ManagementNavGraph.kt
package com.gentlelady.reborn.feature.management

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.feature.management.scheduled_feed.ScheduledFeedScreen
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedIntent
import com.gentlelady.reborn.management.scheduled_feed.presentation.ScheduledFeedState

/**
 * "관리" 섹션 하위 메뉴(예약된 피드/리마인드, 보관, 저장 등)의 화면들을 모아두는 서브 네비게이션 그래프.
 * MyProfile의 관리 그리드에서 진입하며, 메뉴가 늘어날 때마다 이 파일에 route를 추가한다.
 */
fun NavGraphBuilder.managementNavGraph(
    scheduledFeedState: ScheduledFeedState,
    onScheduledFeedIntent: (ScheduledFeedIntent) -> Unit
) {
    composable("management/scheduled_feed") {
        ScheduledFeedScreen(
            state = scheduledFeedState,
            onIntent = onScheduledFeedIntent
        )
    }
}
