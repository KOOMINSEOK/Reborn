package com.gentlelady.reborn.feature.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.profile.presentation.ProfileIntent
import com.gentlelady.reborn.profile.presentation.ProfileState

/**
 * 프로필 피처 내부의 모든 화면 흐름을 관장하는 독립 서브 라우터 (ToC 규칙 준수)
 */
fun NavGraphBuilder.profileNavGraph(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit
) {
    // 하위 탭바 depth에서 진입하는 프로필 메인 포인터
    composable("profile") {
        ProfileScreen(
            state = state,
            onIntent = onIntent
        )
    }

    // 💡 추후 "profile_edit", "posthumous_manager_setting" 등 서브 데스티네이션이 생기면 여기에 추가합니다.
}