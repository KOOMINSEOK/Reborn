package com.gentlelady.reborn.feature.management.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.designsystem.components.GroupedOptionList
import com.gentlelady.reborn.core.designsystem.components.OptionListIcon
import com.gentlelady.reborn.core.designsystem.components.OptionListItem
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornSlateGray
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * "공개 범위 및 보안" 진입 화면. 하위 메뉴가 늘어날수록 이 리스트에 항목만 추가하면 된다.
 */
@Composable
fun SecurityScreen(
    onBackClick: () -> Unit,
    onClickAccountVisibility: () -> Unit,
    onClickDeviceManagement: () -> Unit,
    onClickChangePassword: () -> Unit,
    onClickBlockedAccounts: () -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "공개 범위 및 보안", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            GroupedOptionList(
                items = listOf(
                    OptionListItem(
                        label = "계정 공개 범위",
                        icon = OptionListIcon.Vector(Icons.Default.Lock),
                        onClick = onClickAccountVisibility
                    ),
                    OptionListItem(
                        label = "로그인 기기 관리",
                        icon = OptionListIcon.Vector(Icons.Default.PhoneAndroid),
                        onClick = onClickDeviceManagement
                    ),
                    OptionListItem(
                        label = "비밀번호 변경",
                        icon = OptionListIcon.Vector(Icons.Default.VpnKey),
                        onClick = onClickChangePassword
                    ),
                    OptionListItem(
                        label = "차단 계정",
                        icon = OptionListIcon.Vector(Icons.Default.Block),
                        onClick = onClickBlockedAccounts
                    )
                )
            )

            Text(
                text = "개인정보와 계정을 안전하게 관리하세요.",
                color = RebornSlateGray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 12.dp, start = 4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun SecurityScreenPreview() {
    MaterialTheme {
        SecurityScreen(
            onBackClick = {},
            onClickAccountVisibility = {},
            onClickDeviceManagement = {},
            onClickChangePassword = {},
            onClickBlockedAccounts = {}
        )
    }
}
