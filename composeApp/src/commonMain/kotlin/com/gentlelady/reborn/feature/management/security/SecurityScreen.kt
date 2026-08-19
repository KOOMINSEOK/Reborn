package com.gentlelady.reborn.feature.management.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.GroupedOptionList
import com.gentlelady.reborn.core.designsystem.components.OptionListIcon
import com.gentlelady.reborn.core.designsystem.components.OptionListItem
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornGridIconGray
import com.gentlelady.reborn.core.theme.RebornIconBoxBlue
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.ic_key
import com.gentlelady.reborn.ic_lock
import com.gentlelady.reborn.ic_mobile
import com.gentlelady.reborn.ic_prohibited
import com.gentlelady.reborn.ic_shield
import org.jetbrains.compose.resources.painterResource
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
                        icon = OptionListIcon.Drawable(Res.drawable.ic_lock),
                        onClick = onClickAccountVisibility
                    ),
                    OptionListItem(
                        label = "로그인 기기 관리",
                        icon = OptionListIcon.Drawable(Res.drawable.ic_mobile),
                        onClick = onClickDeviceManagement
                    ),
                    OptionListItem(
                        label = "비밀번호 변경",
                        icon = OptionListIcon.Drawable(Res.drawable.ic_key),
                        onClick = onClickChangePassword
                    ),
                    OptionListItem(
                        label = "차단 계정",
                        icon = OptionListIcon.Drawable(Res.drawable.ic_prohibited),
                        onClick = onClickBlockedAccounts
                    )
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = RebornGridIconGray, shape = RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color = RebornIconBoxBlue, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_shield),
                        contentDescription = null,
                        tint = RebornDeepBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "개인정보와 계정 보안을 안전하게 관리하세요.",
                    color = RebornSlateGray,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
            }
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
