package com.gentlelady.reborn.feature.memorial.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.memorial.presentation.MemorialTab
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MemorialTabBar(
    selectedTab: MemorialTab,
    onTabSelect: (MemorialTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // 1. 탭 버튼 영역 (50% : 50% 균등 분할)
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val totalWidth = maxWidth
            val tabWidth = totalWidth / 2

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 히스토리 탭
                MemorialTabItem(
                    title = "히스토리",
                    isSelected = selectedTab == MemorialTab.HISTORY,
                    onClick = { onTabSelect(MemorialTab.HISTORY) },
                    modifier = Modifier.weight(1f)
                )

                // 방명록 탭
                MemorialTabItem(
                    title = "방명록",
                    isSelected = selectedTab == MemorialTab.GUESTBOOK,
                    onClick = { onTabSelect(MemorialTab.GUESTBOOK) },
                    modifier = Modifier.weight(1f)
                )
            }

            // 2. 하단 슬라이딩 파란색 인디케이터 라인
            val indicatorOffset by animateDpAsState(
                targetValue = if (selectedTab == MemorialTab.HISTORY) 0.dp else tabWidth,
                animationSpec = tween(durationMillis = 200),
                label = "TabIndicatorAnimation"
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = indicatorOffset)
                    .width(tabWidth)
                    .height(2.5.dp)
                    .background(RebornCobaltBlue)
            )
        }

        // 3. 탭바 하단 구분선
        HorizontalDivider(
            color = RebornDividerGray,
            thickness = 1.dp
        )
    }
}

/**
 * 개별 탭 아이템 (지역 함수 중첩 금지 규칙에 따라 파일 레벨 분리)
 */
@Composable
private fun MemorialTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(44.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // 터치 리플 잔상 제거하여 깔끔하게 처리
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) RebornCobaltBlue else RebornSlateGray
        )
    }
}

// Direct Injection 프리뷰 규칙 준수 (Provider/MockDataSource 차단)
@Preview
@Composable
private fun MemorialTabBarHistoryPreview() {
    MaterialTheme {
        Surface {
            MemorialTabBar(
                selectedTab = MemorialTab.HISTORY,
                onTabSelect = {}
            )
        }
    }
}

@Preview
@Composable
private fun MemorialTabBarGuestBookPreview() {
    MaterialTheme {
        Surface {
            MemorialTabBar(
                selectedTab = MemorialTab.GUESTBOOK,
                onTabSelect = {}
            )
        }
    }
}