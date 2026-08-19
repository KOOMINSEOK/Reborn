package com.gentlelady.reborn.feature.management.profile_edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornMyBubbleBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornUnselectedGray
import com.gentlelady.reborn.myprofile.presentation.MyProfileState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BasicInfoManagementScreen(
    profileState: MyProfileState,
    onBackClick: () -> Unit,
    onClickNickname: () -> Unit,
    onClickProfilePhoto: () -> Unit,
    onClickBio: () -> Unit
) {
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
                Box(modifier = Modifier.size(112.dp)) {
                    profileState.profileImageUrl?.let { profileRes ->
                        Image(
                            painter = painterResource(profileRes),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Box(
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
            onClickProfilePhoto = {},
            onClickBio = {}
        )
    }
}
