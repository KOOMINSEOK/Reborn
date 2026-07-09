package com.gentlelady.reborn.feature.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.ic_lock
import com.gentlelady.reborn.profile.presentation.ProfileState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
internal fun ProfileHeaderSection(
    state: ProfileState,
    onEditBackgroundClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // 1. 상단 풀블리드 커버 이미지 영역 (그라데이션 오버레이 포함)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            state.backgroundImageUrl?.let { bgRes ->
                Image(
                    painter = painterResource(bgRes),
                    contentDescription = "Cover Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } ?: Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RebornDividerGray)
            )

            // 그라데이션 오버레이 (상단 딤 처리)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent),
                            endY = 300f
                        )
                    )
            )

            // 배경 편집 버튼 (우하단 배치)
            Button(
                onClick = onEditBackgroundClick,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text(
                    text = "배경 편집",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 2. 프로필 아바타 및 사용자 지표 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // 프로필 이미지 및 아바타 겹침 구조
            Box(
                modifier = Modifier.size(88.dp)
            ) {
                state.profileImageUrl?.let { profileRes ->
                    Image(
                        painter = painterResource(profileRes),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .align(Alignment.TopStart),
                        contentScale = ContentScale.Crop
                    )
                } ?: Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.LightGray)
                        .align(Alignment.TopStart)
                )

                // 아바타 우하단 카메라 아이콘/포인트 배지 (시안 반영)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(RebornDeepBlue, shape = CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    // 예시 카메라 또는 사진 아이콘 대체 가능
                    Box(modifier = Modifier.size(10.dp).background(Color.White, CircleShape))
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            // 3가지 지표 영역 (FEED, FOLLOWERS, FOLLOWING)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileMetricItem(
                    count = state.posthumousFeedCount.toString(),
                    label = "FEED",
                    showLock = true
                )
                ProfileMetricItem(
                    count = state.followersCount.toString(),
                    label = "FOLLOWERS",
                    showLock = false
                )
                ProfileMetricItem(
                    count = state.followingCount.toString(),
                    label = "FOLLOWING",
                    showLock = false
                )
            }
        }

        // 유저네임 텍스트 영역
        Text(
            text = "@${state.username}",
            modifier = Modifier
                .padding(horizontal = 24.dp) // 1단계: 좌우 가로 패딩 먼저 적용
                .padding(bottom = 24.dp),    // 2단계: 하단 패딩 연속 체이닝으로 결합
            color = RebornUnselectedGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun RowScope.ProfileMetricItem(
    count: String,
    label: String,
    showLock: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showLock) {
            // 사후 피드를 상징하는 코발트 블루 자물쇠 배지 배치 규칙 준수
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(RebornSoftBlue, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_lock),
                    contentDescription = "Posthumous Feed",
                    tint = RebornDeepBlue,
                    modifier = Modifier.size(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
        } else {
            // 레이아웃 높이 밸런스를 맞추기 위한 투명 플레이스홀더
            Spacer(modifier = Modifier.height(22.dp))
        }

        Text(
            text = count,
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = RebornUnselectedGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// --- 프리뷰 규칙 준수: PreviewParameterProvider 차단 및 Direct Injection 데이터 적용 ---
@Preview
@Composable
private fun ProfileHeaderSectionPreview() {
    val previewState = ProfileState(
        username = "hong_gild",
        posthumousFeedCount = 5,
        followersCount = 248,
        followingCount = 91,
        profileImageUrl = null,  // KMP 렌더러 안전성 확보를 위해 null 처리 후 플레이스홀더 검증
        backgroundImageUrl = null
    )
    MaterialTheme {
        ProfileHeaderSection(
            state = previewState,
            onEditBackgroundClick = {}
        )
    }
}