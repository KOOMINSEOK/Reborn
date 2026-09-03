package com.gentlelady.reborn.feature.message.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // by 위임 사용을 위한 필수 임포트
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.ic_write // 범용 명칭 에셋 규칙 준수
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.message.presentation.MessageTab
import org.jetbrains.compose.resources.painterResource

/**
 * Reborn 프로젝트 메시지 화면 전용 캡슐화 상단 바
 * 규칙: feature/{name}/components/ 내부에 위치하며 internal로 은닉
 */
@Composable
internal fun MessageTopAppBar(
    currentTab: MessageTab,
    onSearchClick: () -> Unit,
    onTabSelect: (MessageTab) -> Unit,
    onWriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // 1. 상단 타이틀 및 작성 버튼 액션 행
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (currentTab == MessageTab.MESSAGE) "메시지" else "방명록",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            IconButton(
                onClick = onWriteClick,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_write),
                    contentDescription = "작성하기 버튼",
                    tint = RebornSlateGray
                )
            }
        }

        // 2. 가변형 커스텀 탭바 (각 탭 너비 정확히 45% 할당 버전)
        MessageTabBar(
            selectedTab = currentTab,
            onTabSelect = onTabSelect
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. 검색창 (탭 클릭 시 전용 검색 화면으로 이동, 여기서는 입력 불가한 버튼 형태)
        MessageSearchBar(
            currentTab = currentTab,
            onClick = onSearchClick,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

/**
 * 2단계 분화: 커스텀 검색 바 컴포저블
 */
@Composable
private fun MessageSearchBar(
    currentTab: MessageTab,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(22.dp),
        color = RebornLightBlueBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "검색",
                tint = RebornSlateGray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (currentTab == MessageTab.MESSAGE) "사용자 검색..." else "방명록 기록 검색...",
                fontSize = 14.sp,
                color = RebornSlateGray
            )
        }
    }
}

/**
 * 2단계 분화: 너비 45% 고정형 커스텀 탭바 컴포저블
 * 컴파일러 경고 및 오작동 요소 (.bind() 등 오타 매커니즘) 완전 청소 완료
 */
@Composable
private fun MessageTabBar(
    selectedTab: MessageTab,
    onTabSelect: (MessageTab) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        val totalWidth = maxWidth

        // 🔔 요청하신 5% : 45% : 45% : 5% 규격 연산 정밀화
        val tabWidthDp = totalWidth * 0.45f
        val startMarginDp = totalWidth * 0.05f

        // 하단 인디케이터가 중간 공백 오차 없이 정확히 자기 탭 크기(45%)만큼만 X축 이동하도록 공식 수정
        val indicatorOffsetDp by animateDpAsState(
            targetValue = if (selectedTab == MessageTab.MESSAGE) {
                startMarginDp
            } else {
                startMarginDp + tabWidthDp // 시작 마진(5%) + 메시지 탭 너비(45%) 지점으로 바로 안착
            },
            animationSpec = tween(durationMillis = 250),
            label = "TabIndicatorAnimation"
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. 메인 탭 컨테이너 (정확히 5% : 45% : 45% : 5% 레이아웃 보장)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 왼쪽 마진 5%
                Spacer(modifier = Modifier.weight(0.05f))

                // [메시지 탭] 너비 45% 차지
                Box(
                    modifier = Modifier
                        .weight(0.45f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onTabSelect(MessageTab.MESSAGE)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "메시지",
                        fontSize = 15.sp,
                        fontWeight = if (selectedTab == MessageTab.MESSAGE) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == MessageTab.MESSAGE) RebornCobaltBlue else RebornSlateGray
                    )
                }

                // [방명록 탭] 너비 45% 차지 (중간 Spacer를 삭제하여 두 탭이 센터에서 45%씩 정확히 맞물림)
                Box(
                    modifier = Modifier
                        .weight(0.45f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onTabSelect(MessageTab.GUEST_BOOK)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "방명록",
                        fontSize = 15.sp,
                        fontWeight = if (selectedTab == MessageTab.GUEST_BOOK) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == MessageTab.GUEST_BOOK) RebornCobaltBlue else RebornSlateGray
                    )
                }

                // 오른쪽 마진 5%
                Spacer(modifier = Modifier.weight(0.05f))
            }

            // 2. 하단 지시선 영역 (45% 컴팩트 바바인딩 매칭 완료)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
            ) {
                // 전체 가로 지르는 연회색 구분선 [cite: 1230]
                HorizontalDivider(
                    color = RebornDividerGray,
                    thickness = 1.dp,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )

                // 위 상단 글자 영역 45% 뼈대 폭과 한치의 오차도 없이 맞물려 흐르는 Cobalt Blue 라인
                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffsetDp)
                        .width(tabWidthDp)
                        .fillMaxHeight()
                        .background(
                            color = RebornCobaltBlue,
                            shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                        )
                )
            }
        }
    }
}
// --- 프리뷰 규칙 엄격 준수: MockDataSource 참조 원천 차단형 수동 직접 주입 검증 완료 ---
@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun MessageTopAppBarMessageTabPreview() {
    MaterialTheme {
        Surface {
            MessageTopAppBar(
                currentTab = MessageTab.MESSAGE,
                onSearchClick = {},
                onTabSelect = {},
                onWriteClick = {}
            )
        }
    }
}
@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun MessageTopAppBarGuestBookTabPreview() {
    MaterialTheme {
        Surface {
            MessageTopAppBar(
                currentTab = MessageTab.GUEST_BOOK,
                onSearchClick = {},
                onTabSelect = {},
                onWriteClick = {}
            )
        }
    }
}