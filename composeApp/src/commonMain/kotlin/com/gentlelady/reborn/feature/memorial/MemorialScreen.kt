// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/memorial/MemorialScreen.kt
package com.gentlelady.reborn.feature.memorial

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.CircleIconBadge
import com.gentlelady.reborn.core.designsystem.components.ImageGridAlbum
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.data.MemorialMockData
import com.gentlelady.reborn.feature.memorial.components.MemorialHeaderSection
import com.gentlelady.reborn.feature.memorial.components.MemorialTabBar
import com.gentlelady.reborn.feature.memorial.guestbook.MemorialGuestBookList
import com.gentlelady.reborn.feature.memorial.history.MemorialHistoryDetailScreen
import com.gentlelady.reborn.feature.memorial.history.MemorialHistoryWriteScreen
import com.gentlelady.reborn.ic_flower_plant
import com.gentlelady.reborn.ic_share
import com.gentlelady.reborn.memorial.presentation.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialScreen(
    state: MemorialState,
    onIntent: (MemorialIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedHistoryItem = state.selectedHistoryIndex?.let { state.historyItems.getOrNull(it) }

    // 💡 state.isEditingProfile / selectedHistoryIndex 플래그에 따라 하위 화면으로 즉시 전환 연동
    if (state.isEditingProfile) {
        MemorialEditProfileScreen(
            formState = state.editFormState,
            onIntent = onIntent,
            modifier = modifier
        )
    } else if (selectedHistoryItem != null) {
        MemorialHistoryDetailScreen(
            item = selectedHistoryItem,
            onIntent = onIntent,
            modifier = modifier
        )
    } else if (state.isWritingHistory) {
        MemorialHistoryWriteScreen(
            formState = state.historyWriteFormState,
            onIntent = onIntent,
            modifier = modifier
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = Color.White,
            contentWindowInsets = WindowInsets(0.dp), // 바깥 MainScreen Scaffold가 이미 하단 인셋을 처리하므로 중복 방지
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { onIntent(MemorialIntent.ClickBack) }) {
                            CircleIconBadge(
                                icon = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "뒤로가기",
                                size = 36.dp
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { onIntent(MemorialIntent.ClickShare) }) {
                            CircleIconBadge(
                                icon = Res.drawable.ic_share,
                                contentDescription = "공유",
                                size = 36.dp,
                                iconSize = 18.dp
                            )
                        }
                        IconButton(onClick = { onIntent(MemorialIntent.ClickAddHistory) }) {
                            CircleIconBadge(
                                icon = Icons.Filled.Add,
                                contentDescription = "히스토리 작성",
                                size = 36.dp
                            )
                        }
                        IconButton(onClick = { onIntent(MemorialIntent.ClickMusic) }) {
                            CircleIconBadge(
                                icon = Res.drawable.ic_flower_plant,
                                contentDescription = "메모리얼 마크",
                                size = 36.dp,
                                iconTint = RebornCobaltBlue
                            )
                        }
                        IconButton(onClick = { onIntent(MemorialIntent.ClickMore) }) {
                            CircleIconBadge(
                                icon = Icons.Filled.MoreHoriz,
                                contentDescription = "더보기",
                                size = 36.dp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    ),
                    windowInsets = WindowInsets(0.dp)
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
                    onEditProfileClick = { onIntent(MemorialIntent.ClickEditProfile) }
                )

                // 2. 히스토리 / 온라인 화환 / 방명록 탭바
                MemorialTabBar(
                    selectedTab = state.selectedTab,
                    historyCount = state.historyCount,
                    onlineWreathCount = state.onlineWreathCount,
                    guestBookCount = state.guestBookCount,
                    onTabSelect = { tab -> onIntent(MemorialIntent.SelectTab(tab)) }
                )

                // 3. 탭별 콘텐츠 영역 (탭바와 동일한 좌우 여백)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    when (state.selectedTab) {
                        MemorialTab.HISTORY -> {
                            ImageGridAlbum(
                                images = state.historyItems.map { it.imageRes },
                                onImageClick = { index ->
                                    onIntent(MemorialIntent.ClickHistoryImage(index))
                                },
                                emptyMessage = "등록된 히스토리 사진이 없습니다.",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        MemorialTab.ONLINE_WREATH -> {
                            ImageGridAlbum(
                                images = state.onlineWreathImages,
                                onImageClick = {},
                                emptyMessage = "아직 도착한 온라인 화환이 없습니다.",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        MemorialTab.GUESTBOOK -> {
                            MemorialGuestBookList(
                                guestBookMessages = state.guestBookMessages,
                                inputText = state.guestBookInputText,
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
        profile = MemorialMockData.myMemorialState.profile,
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
        profile = MemorialMockData.otherMemorialState.profile,
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
        profile = MemorialMockData.otherMemorialState.profile,
        selectedTab = MemorialTab.HISTORY,
        historyItems = MemorialMockData.historyItems
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
        profile = MemorialMockData.myMemorialState.profile,
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
        profile = MemorialMockData.myMemorialState.profile,
        selectedTab = MemorialTab.HISTORY,
        historyItems = MemorialMockData.historyItems
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