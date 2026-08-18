package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornTopBarDividerGray
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 뒤로가기(또는 닫기) + 가운데 정렬 제목 + 하단 구분선으로 이루어진 공용 상단바.
 * CenterAlignedTopAppBar를 써서 좌측 아이콘 유무와 상관없이 제목이 화면 정중앙에 고정된다.
 * 네비게이션 아이콘은 배경 없이 아이콘만 노출된다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RebornBackTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    iconContentDescription: String = "뒤로가기"
) {
    Column(modifier = modifier) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = icon,
                        contentDescription = iconContentDescription,
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
            windowInsets = WindowInsets(0.dp)
        )
        HorizontalDivider(color = RebornTopBarDividerGray, thickness = 1.dp)
    }
}

@Preview
@Composable
private fun RebornBackTopAppBarPreview() {
    MaterialTheme {
        Surface {
            RebornBackTopAppBar(title = "결제하기", onBackClick = {})
        }
    }
}

@Preview
@Composable
private fun RebornCloseTopAppBarPreview() {
    MaterialTheme {
        Surface {
            RebornBackTopAppBar(
                title = "로그인된 기기",
                onBackClick = {},
                icon = Icons.Default.Close,
                iconContentDescription = "닫기"
            )
        }
    }
}
