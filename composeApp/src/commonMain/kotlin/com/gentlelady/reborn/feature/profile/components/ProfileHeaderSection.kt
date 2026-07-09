package com.gentlelady.reborn.feature.profile.components

import androidx.compose.foundation.BorderStroke
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
import com.gentlelady.reborn.ic_camera
import com.gentlelady.reborn.ic_home_memorial
import com.gentlelady.reborn.img_memorial_bg_dummy
import com.gentlelady.reborn.img_memorial_profile_dummy
import com.gentlelady.reborn.profile.presentation.ProfileState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ProfileHeaderSection(
    state: ProfileState,
    onEditBackgroundClick: () -> Unit,
    onToggleMemorialModeClick: () -> Unit,
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

            // ✅ [수정] 우측 상단 원형 메모리얼 토글 버튼 (배경 30%, 흰색 테두리 25% 적용)
            Button(
                onClick = onToggleMemorialModeClick,
                modifier = Modifier
                    .padding(top = 16.dp, end = 16.dp)
                    .align(Alignment.TopEnd)
                    .size(36.dp), // Modifier.border를 완전히 제거
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black.copy(alpha = 0.3f)
                ),
                shape = CircleShape,
                // ⭐ 컴포즈 내장 border 파라미터를 사용하여 배경에 칼같이 밀착
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_home_memorial),
                    contentDescription = "Toggle Memorial Mode",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            // ✅ [수정] 우측 하단 배경 편집 버튼 (배경 40%, 흰색 테두리 25% 적용)
            Button(
                onClick = onEditBackgroundClick,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 16.dp)
                    .align(Alignment.BottomEnd)
                    .height(36.dp), // Modifier.border를 완전히 제거
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                contentPadding = PaddingValues(horizontal = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_camera),
                        contentDescription = "Edit Background Icon",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "배경 편집",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 2. 프로필 아바타 및 사용자 지표 영역 (정렬 위계 완벽 복구)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 16.dp,
                    bottom = 0.dp
                ),
            verticalAlignment = Alignment.Bottom // 아바타 하단선과 지표 텍스트 하단 정렬 싱크 맞춤
        ) {
            // 프로필 이미지 및 아바타 겹침 구조 Box
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

                // 아바타 우하단 카메라 포인트 배지
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(RebornDeepBlue, shape = CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_camera),
                        contentDescription = "Change Profile Photo Icon",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 3가지 지표 영역 (FEED, FOLLOWERS, FOLLOWING) -> Row 내부로 격리 안착
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 10.dp),
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
        } // ◀ Row의 명확한 폐쇄 앵커

        // 3. 유저네임 텍스트 영역 (부모 Column의 직계 자식으로 올바르게 배치)
        Text(
            text = "@${state.username}",
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .padding(top = 4.dp),
            color = RebornUnselectedGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    } // ◀ 부모 Column의 명확한 폐쇄 앵커
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

@Preview
@Composable
private fun ProfileHeaderSectionPreview() {
    val previewState = ProfileState(
        username = "hong_gild",
        posthumousFeedCount = 5,
        followersCount = 248,
        followingCount = 91,
        profileImageUrl = Res.drawable.img_memorial_profile_dummy,
        backgroundImageUrl = Res.drawable.img_memorial_bg_dummy
    )
    MaterialTheme {
        ProfileHeaderSection(
            state = previewState,
            onEditBackgroundClick = {},
            onToggleMemorialModeClick = {}
        )
    }
}