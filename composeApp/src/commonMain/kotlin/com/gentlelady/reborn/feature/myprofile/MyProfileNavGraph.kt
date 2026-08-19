// feature/myprofile/MyProfileNavGraph.kt
package com.gentlelady.reborn.feature.myprofile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gentlelady.reborn.myprofile.presentation.MyProfileIntent
import com.gentlelady.reborn.myprofile.presentation.MyProfileState

fun NavGraphBuilder.myProfileNavGraph(
    state: MyProfileState,
    onIntent: (MyProfileIntent) -> Unit
) {
    composable("profile") {
        MyProfileScreen(
            state = state,
            onIntent = onIntent // 클릭 시 발생한 MyProfileIntent를 상위로 바로 전달
        )
    }
}
