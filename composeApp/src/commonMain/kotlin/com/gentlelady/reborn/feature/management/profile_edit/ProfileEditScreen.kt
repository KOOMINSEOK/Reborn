package com.gentlelady.reborn.feature.management.profile_edit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.core.designsystem.components.GroupedOptionList
import com.gentlelady.reborn.core.designsystem.components.OptionListItem
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfileEditScreen(
    onBackClick: () -> Unit,
    onClickBasicInfo: () -> Unit,
    onClickPayment: () -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "프로필 편집", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        GroupedOptionList(
            items = listOf(
                OptionListItem(label = "기본 정보 관리", onClick = onClickBasicInfo),
                OptionListItem(label = "결제 관리", onClick = onClickPayment)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(24.dp)
        )
    }
}

@Preview
@Composable
private fun ProfileEditScreenPreview() {
    MaterialTheme {
        ProfileEditScreen(onBackClick = {}, onClickBasicInfo = {}, onClickPayment = {})
    }
}
