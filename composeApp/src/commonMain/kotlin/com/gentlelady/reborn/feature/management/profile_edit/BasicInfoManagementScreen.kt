package com.gentlelady.reborn.feature.management.profile_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.designsystem.components.GroupedOptionList
import com.gentlelady.reborn.core.designsystem.components.OptionListItem
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.designsystem.components.RebornImage
import com.gentlelady.reborn.core.designsystem.components.hasImage
import com.gentlelady.reborn.core.platform.rememberGalleryImagePicker
import com.gentlelady.reborn.core.theme.RebornBlack
import com.gentlelady.reborn.core.theme.RebornDangerRed
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornMyBubbleBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornUnselectedGray
import com.gentlelady.reborn.core.theme.RebornWhite
import com.gentlelady.reborn.myprofile.presentation.MyProfileState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BasicInfoManagementScreen(
    profileState: MyProfileState,
    onBackClick: () -> Unit,
    onClickNickname: () -> Unit,
    /** 갤러리에서 고른 사진(업로드용 JPEG) */
    onProfilePhotoPicked: (ByteArray) -> Unit,
    onClickBio: () -> Unit
) {
    val launchGalleryPicker = rememberGalleryImagePicker(onImagePicked = onProfilePhotoPicked)
    val onClickProfilePhoto = { if (!profileState.isUpdatingAvatar) launchGalleryPicker() }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "기본 정보 관리", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.size(112.dp).clip(CircleShape).clickable(onClick = onClickProfilePhoto)) {
                    if (hasImage(profileState.avatarUrl, profileState.profileImageUrl)) {
                        RebornImage(
                            url = profileState.avatarUrl,
                            fallback = profileState.profileImageUrl,
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(RebornMyBubbleBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = RebornSlateGray,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    if (profileState.isUpdatingAvatar) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(RebornBlack.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = RebornWhite, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                        }
                    }
                }

                profileState.avatarError?.let { message ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = message, color = RebornDangerRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row {
                    Text(
                        text = profileState.displayName,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "@${profileState.username}",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "기본 프로필 설정",
                    color = RebornUnselectedGray,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            GroupedOptionList(
                items = listOf(
                    OptionListItem(label = "닉네임", onClick = onClickNickname),
                    OptionListItem(label = "프로필 사진", onClick = onClickProfilePhoto),
                    OptionListItem(label = "한 줄 소개", onClick = onClickBio)
                )
            )
        }
    }
}

@Preview
@Composable
private fun BasicInfoManagementScreenPreview() {
    MaterialTheme {
        BasicInfoManagementScreen(
            profileState = MyProfileState(username = "hong_gild", displayName = "홍길동"),
            onBackClick = {},
            onClickNickname = {},
            onProfilePhotoPicked = {},
            onClickBio = {}
        )
    }
}
