// feature/profile/ProfileNavGraph.kt
package com.gentlelady.reborn.feature.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.profile.presentation.ProfileIntent
import com.gentlelady.reborn.profile.presentation.ProfileState

fun NavGraphBuilder.profileNavGraph(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit
) {
    composable("profile") {
        ProfileScreen(
            state = state,
            onIntent = onIntent // 클릭 시 발생한 ProfileIntent를 상위로 바로 전달
        )
    }
}