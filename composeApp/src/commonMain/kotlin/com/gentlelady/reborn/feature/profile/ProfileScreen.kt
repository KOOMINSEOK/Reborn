package com.gentlelady.reborn.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.ic_down_arrow
import com.gentlelady.reborn.feature.profile.components.*
import com.gentlelady.reborn.img_memorial_bg_dummy
import com.gentlelady.reborn.profile.domain.model.ProfileFeedItem
import com.gentlelady.reborn.profile.presentation.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfileScreen(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0.dp) // 바깥 MainScreen Scaffold가 이미 하단 인셋을 처리하므로 중복 방지
    ) { paddingValues ->
        // 디바이스의 첫 장 가두기 화면 높이를 계산하기 위해 BoxWithConstraints 유지
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding()) // 하단 탭바 영역 확보
        ) {
            val screenHeight = maxHeight

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // [첫 번째 뷰포트]: 진입 시 화면 가득 피드 캐로셀까지 가두는 영역
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = screenHeight) // 첫 장은 무조건 기기 전체 높이 확보
                            .background(Color.White),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // 1. 상단 배경, 프로필 아바타, 지표 정보 섹션
                            ProfileHeaderSection(
                                state = state,
                                onEditProfileClick = { onIntent(ProfileIntent.ClickEditProfile) }
                            )

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 24.dp,
                                        end = 24.dp,
                                        top = 0.dp,
                                        bottom = 16.dp
                                    ),
                                thickness = 1.dp,
                                color = RebornGridIconGray
                            )

                            ProfileFeedCarousel(
                                feeds = state.feeds,
                                onViewAllClick = { onIntent(ProfileIntent.ClickViewAllFeeds) }
                            )
                        }

                        // 3. 하단 [ic_down_arrow + Scroll for more] 안내 영역
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_down_arrow),
                                contentDescription = "Scroll for more indicator",
                                tint = RebornUnselectedGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Scroll for more",
                                color = RebornUnselectedGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // [두 번째 뷰포트]: 스크롤을 내렸을 때 노출되는 영역
                item {
                    // 💡 하단에 불필요한 빈 여백이 남지 않도록 고정/최소 높이 제한을 완전히 제거
                    ProfileManagementGrid(
                        scheduledCount = state.scheduledFeedCount,
                        onMenuClick = { menuId -> onIntent(ProfileIntent.ClickManagementMenu(menuId)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp) // 최하단선과 카드 사이의 숨통 트기용 컴팩트 마진만 배정
                    )
                }
            }
        }
    }
}

// --- 프리뷰 규칙 준수: PreviewParameterProvider 차단 및 Direct Injection 데이터 적용 ---
@Preview
@Composable
private fun ProfileScreenPreview() {
    val previewState = ProfileState(
        username = "hong_gild",
        displayName = "홍길동",
        posthumousFeedCount = 5,
        followersCount = 248,
        followingCount = 91,
        scheduledFeedCount = 3,
        feeds = listOf(
            ProfileFeedItem("1", "가을 산책을 하며", "예전에도 요즘에도 산책하는걸 좋아하는데요...", Res.drawable.img_memorial_bg_dummy, 24, 6)
        )
    )
    MaterialTheme {
        ProfileScreen(
            state = previewState,
            onIntent = {}
        )
    }
}