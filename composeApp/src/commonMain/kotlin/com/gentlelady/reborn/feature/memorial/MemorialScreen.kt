// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/memorial/MemorialScreen.kt
package com.gentlelady.reborn.feature.memorial

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.data.MemorialMockData
import com.gentlelady.reborn.feature.memorial.components.MemorialGuestBookList
import com.gentlelady.reborn.feature.memorial.components.MemorialHeaderSection
import com.gentlelady.reborn.feature.memorial.components.MemorialHistoryGrid
import com.gentlelady.reborn.feature.memorial.components.MemorialTabBar
import com.gentlelady.reborn.memorial.presentation.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialScreen(
    state: MemorialState,
    onIntent: (MemorialIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    // 💡 state.isEditingProfile 플래그에 따라 프로필 편집 화면으로 즉시 전환 연동
    if (state.isEditingProfile) {
        MemorialEditProfileScreen(
            formState = state.editFormState,
            onIntent = onIntent,
            modifier = modifier
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = Color.White,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (state.ownerType == MemorialOwnerType.MY_MEMORIAL) "내 공간" else state.profile.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onIntent(MemorialIntent.ClickBack) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "뒤로가기",
                                tint = Color.Black
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { onIntent(MemorialIntent.ClickMusic) }) {
                            Icon(
                                imageVector = Icons.Filled.MusicNote,
                                contentDescription = "배경음악",
                                tint = RebornCobaltBlue
                            )
                        }
                        IconButton(onClick = { onIntent(MemorialIntent.ClickMore) }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "더보기",
                                tint = RebornSlateGray
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 1. 프로필 헤더 영역
                MemorialHeaderSection(
                    profile = state.profile,
                    ownerType = state.ownerType,
                    onEditProfileClick = { onIntent(MemorialIntent.ClickEditProfile) }
                )

                // 2. 히스토리 / 방명록 탭바
                MemorialTabBar(
                    selectedTab = state.selectedTab,
                    onTabSelect = { tab -> onIntent(MemorialIntent.SelectTab(tab)) }
                )

                // 3. 탭별 콘텐츠 영역
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (state.selectedTab) {
                        MemorialTab.HISTORY -> {
                            MemorialHistoryGrid(
                                images = state.historyImages,
                                onImageClick = { index ->
                                    onIntent(MemorialIntent.ClickHistoryImage(index))
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        MemorialTab.GUESTBOOK -> {
                            MemorialGuestBookList(
                                guestBookMessages = state.guestBookMessages,
                                inputText = state.guestBookInputText,
                                ownerType = state.ownerType,
                                onInputTextChange = { text ->
                                    onIntent(MemorialIntent.UpdateGuestBookInput(text))
                                },
                                onSubmitClick = {
                                    onIntent(MemorialIntent.SubmitGuestBook)
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MemorialScreenMyEditProfilePreview() {
    val dummyState = MemorialState(
        ownerType = MemorialOwnerType.MY_MEMORIAL,
        profile = MemorialProfileData(
            name = "이윤주",
            handle = "uexjurjece",
            bio = "Forever in our hearts, guiding us with love and light.",
            followerCount = 12
        ),
        isEditingProfile = true, // 편집 상태 모드 프리뷰
        editFormState = EditProfileFormState(
            name = "이윤주",
            handle = "uexjurjece",
            bio = "Forever in our hearts, guiding us with love and light.",
            profileImageRes = MemorialMockData.dummyProfileRes
        )
    )

    MaterialTheme {
        Surface {
            MemorialScreen(
                state = dummyState,
                onIntent = {}
            )
        }
    }
}

// Direct Injection 프리뷰 규칙 준수
@Preview
@Composable
private fun MemorialScreenOtherViewPreview() {
    val dummyState = MemorialState(
        ownerType = MemorialOwnerType.OTHER_MEMORIAL,
        profile = MemorialProfileData(
            name = "홍길동",
            handle = "uexjurjece",
            bio = "인생, 헤맨만큼 내 땅이다",
            followerCount = 5
        ),
        selectedTab = MemorialTab.GUESTBOOK,
        guestBookMessages = MemorialMockData.guestBookMessages
    )

    MaterialTheme {
        Surface {
            MemorialScreen(
                state = dummyState,
                onIntent = {}
            )
        }
    }
}

@Preview
@Composable
private fun MemorialScreenOtherGridViewPreview() {
    val dummyState = MemorialState(
        ownerType = MemorialOwnerType.OTHER_MEMORIAL,
        profile = MemorialProfileData(
            name = "홍길동",
            handle = "uexjurjece",
            bio = "인생, 헤맨만큼 내 땅이다",
            followerCount = 5
        ),
        selectedTab = MemorialTab.HISTORY,
        historyImages = MemorialMockData.historyImages
    )

    MaterialTheme {
        Surface {
            MemorialScreen(
                state = dummyState,
                onIntent = {}
            )
        }
    }
}

@Preview
@Composable
private fun MemorialScreenMyViewPreview() {
    val dummyState = MemorialState(
        ownerType = MemorialOwnerType.MY_MEMORIAL,
        profile = MemorialProfileData(
            name = "이윤주",
            handle = "uexjurjece",
            bio = "인생, 헤맨만큼 내 땅이다",
            followerCount = 12
        ),
        selectedTab = MemorialTab.GUESTBOOK,
        guestBookMessages = MemorialMockData.guestBookMessages
    )

    MaterialTheme {
        Surface {
            MemorialScreen(
                state = dummyState,
                onIntent = {}
            )
        }
    }
}

@Preview
@Composable
private fun MemorialScreenMyGridViewPreview() {
    val dummyState = MemorialState(
        ownerType = MemorialOwnerType.MY_MEMORIAL,
        profile = MemorialProfileData(
            name = "이윤주",
            handle = "uexjurjece",
            followerCount = 12
        ),
        selectedTab = MemorialTab.HISTORY,
        historyImages = MemorialMockData.historyImages
    )

    MaterialTheme {
        Surface {
            MemorialScreen(
                state = dummyState,
                onIntent = {}
            )
        }
    }
}