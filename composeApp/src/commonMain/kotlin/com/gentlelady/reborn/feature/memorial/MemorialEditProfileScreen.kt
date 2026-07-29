// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/memorial/MemorialEditProfileScreen.kt
package com.gentlelady.reborn.feature.memorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.data.MemorialMockData
import com.gentlelady.reborn.memorial.presentation.EditProfileFormState
import com.gentlelady.reborn.memorial.presentation.MemorialIntent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialEditProfileScreen(
    formState: EditProfileFormState,
    onIntent: (MemorialIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "프로필 편집",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(MemorialIntent.ClickCloseEditProfile) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 1. 프로필 사진 변경 섹션 (img_profile_dummy 에셋 적용)
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .size(96.dp)
                    .clickable { onIntent(MemorialIntent.ClickChangeProfileImage) }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = RebornDividerGray
                ) {
                    val imageRes = formState.profileImageRes
                    if (imageRes != null) {
                        Image(
                            painter = painterResource(imageRes), // 스마트 캐스트 정상 적용
                            contentDescription = "프로필 이미지",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = formState.name.take(1),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = RebornSlateGray
                            )
                        }
                    }
                }

                // 우측 하단 카메라 아이콘 배지
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(RebornDeepBlue, CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "사진 바꾸기",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { onIntent(MemorialIntent.ClickChangeProfileImage) }) {
                Text(
                    text = "사진 바꾸기",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RebornDeepBlue
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. 이름 입력 필드 (연한 회색 테두리 적용)
            EditProfileLabel(text = "이름")
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = formState.name,
                onValueChange = { onIntent(MemorialIntent.UpdateEditName(it)) },
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = RebornBackgroundGray,
                    unfocusedContainerColor = RebornBackgroundGray,
                    focusedBorderColor = RebornDeepBlue,
                    unfocusedBorderColor = RebornDividerGray // 은은한 회색 테두리
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. 아이디 (@handle) 입력 필드 (연한 회색 테두리 적용)
            EditProfileLabel(text = "아이디")
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = formState.handle,
                onValueChange = { onIntent(MemorialIntent.UpdateEditHandle(it)) },
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = RebornBackgroundGray,
                    unfocusedContainerColor = RebornBackgroundGray,
                    focusedBorderColor = RebornDeepBlue,
                    unfocusedBorderColor = RebornDividerGray // 은은한 회색 테두리
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "memorialapp.com/@${formState.handle.ifEmpty { "username" }}",
                fontSize = 11.sp,
                color = RebornSlateGray,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. 묘비명 / 한줄 소개 입력 필드 (연한 회색 테두리 적용)
            EditProfileLabel(text = "묘비명/한줄 소개")
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = formState.bio,
                onValueChange = { onIntent(MemorialIntent.UpdateEditBio(it)) },
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = RebornBackgroundGray,
                    unfocusedContainerColor = RebornBackgroundGray,
                    focusedBorderColor = RebornDeepBlue,
                    unfocusedBorderColor = RebornDividerGray // 은은한 회색 테두리
                ),
                minLines = 3,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 5. 변경사항 저장하기 버튼
            Button(
                onClick = { onIntent(MemorialIntent.ClickSaveProfile) },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RebornDeepBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "변경사항 저장하기",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * 필드 라벨 텍스트
 */
@Composable
private fun EditProfileLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = RebornSlateGray,
        modifier = modifier.fillMaxWidth()
    )
}

// Direct Injection 프리뷰 (img_profile_dummy 에셋 적용)
@Preview
@Composable
private fun MemorialEditProfileScreenPreview() {
    MaterialTheme {
        Surface {
            MemorialEditProfileScreen(
                formState = EditProfileFormState(
                    name = "이윤주",
                    handle = "uexjurjence",
                    bio = "Forever in our hearts, guiding us with love and light.",
                    profileImageRes = MemorialMockData.dummyProfileRes
                ),
                onIntent = {}
            )
        }
    }
}