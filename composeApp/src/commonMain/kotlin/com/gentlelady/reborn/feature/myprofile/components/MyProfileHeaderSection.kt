package com.gentlelady.reborn.feature.myprofile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.ic_lock
import com.gentlelady.reborn.ic_camera
import com.gentlelady.reborn.img_memorial_profile_dummy
import com.gentlelady.reborn.myprofile.presentation.MyProfileState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MyProfileHeaderSection(
    state: MyProfileState,
    onBackClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // 1. 상단 바 (뒤로가기만 노출되는 심플한 헤더)
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로가기",
                tint = Color.Black
            )
        }

        // 2. 프로필 아바타 및 사용자 지표 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 프로필 이미지 및 카메라 배지 겹침 구조 Box
            Box(
                modifier = Modifier.size(88.dp)
            ) {
                state.profileImageUrl?.let { profileRes ->
                    Image(
                        painter = painterResource(profileRes),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, RebornGridBorderGray, RoundedCornerShape(20.dp))
                            .align(Alignment.TopStart),
                        contentScale = ContentScale.Crop
                    )
                } ?: Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(RebornDividerGray)
                        .border(1.dp, RebornGridBorderGray, RoundedCornerShape(20.dp))
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = RebornSlateGray,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // 아바타 우하단 카메라 포인트 배지
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(RebornDeepBlue, shape = CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onEditProfileClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_camera),
                            contentDescription = "프로필 사진 변경",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // 3가지 지표 영역 (게시물, 팔로워, 팔로잉)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyProfileMetricItem(
                    count = state.posthumousFeedCount.toString(),
                    label = "게시물",
                    showLock = true
                )
                MyProfileMetricItem(
                    count = state.followersCount.toString(),
                    label = "팔로워",
                    showLock = false
                )
                MyProfileMetricItem(
                    count = state.followingCount.toString(),
                    label = "팔로잉",
                    showLock = false
                )
            }
        }

        // 3. 이름 + 유저네임 텍스트 영역
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 12.dp, bottom = 20.dp)
        ) {
            Text(
                text = state.displayName,
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "@${state.username}",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RowScope.MyProfileMetricItem(
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
            Spacer(modifier = Modifier.height(4.dp))
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = count,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = RebornUnselectedGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview
@Composable
private fun MyProfileHeaderSectionPreview() {
    val previewState = MyProfileState(
        username = "hong_gild",
        displayName = "홍길동",
        posthumousFeedCount = 5,
        followersCount = 248,
        followingCount = 91,
        profileImageUrl = Res.drawable.img_memorial_profile_dummy
    )
    MaterialTheme {
        MyProfileHeaderSection(state = previewState)
    }
}
