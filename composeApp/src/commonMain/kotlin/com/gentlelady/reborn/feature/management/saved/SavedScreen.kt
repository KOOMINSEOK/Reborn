package com.gentlelady.reborn.feature.management.saved

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.GridImageSource
import com.gentlelady.reborn.core.designsystem.components.ImageGridAlbum
import com.gentlelady.reborn.core.designsystem.components.PillTabBar
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.feature.management.saved.components.SavedGuestBookRow
import com.gentlelady.reborn.img_memorial_bg_dummy
import com.gentlelady.reborn.management.saved.domain.model.SavedGridItem
import com.gentlelady.reborn.management.saved.domain.model.SavedGuestBookItem
import com.gentlelady.reborn.management.saved.presentation.SavedIntent
import com.gentlelady.reborn.management.saved.presentation.SavedState
import com.gentlelady.reborn.management.saved.presentation.SavedTab
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SavedScreen(
    state: SavedState,
    onIntent: (SavedIntent) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(
                title = "저장됨",
                onBackClick = { onIntent(SavedIntent.ClickBack) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PillTabBar(
                tabs = listOf("게시물", "히스토리", "방명록"),
                selectedIndex = state.selectedTab.ordinal,
                onTabSelect = { index -> onIntent(SavedIntent.ClickTab(SavedTab.entries[index])) }
            )
            HorizontalDivider(thickness = 1.dp, color = RebornGridBorderGray)

            when (state.selectedTab) {
                // 💡 게시물/히스토리 탭은 동일하게 3xN 정사각 그리드로 노출된다.
                SavedTab.POSTS -> SavedGridContent(
                    items = state.posts,
                    onItemClick = { id -> onIntent(SavedIntent.ClickPost(id)) }
                )
                SavedTab.HISTORY -> SavedGridContent(
                    items = state.history,
                    onItemClick = { id -> onIntent(SavedIntent.ClickHistoryItem(id)) }
                )
                SavedTab.GUESTBOOK -> SavedGuestBookContent(
                    entries = state.guestBookEntries,
                    onEntryClick = { id -> onIntent(SavedIntent.ClickGuestBookEntry(id)) }
                )
            }
        }
    }
}

@Composable
private fun SavedGridContent(
    items: List<SavedGridItem>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ImageGridAlbum(
        images = items.map { GridImageSource.Resource(it.thumbnail) },
        onImageClick = { index -> onItemClick(items[index].id) },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
private fun SavedGuestBookContent(
    entries: List<SavedGuestBookItem>,
    onEntryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        item {
            Text(
                text = "소중한 분들을 향해 남겼던 따뜻한 마음의 기록입니다.",
                color = RebornSlateGray,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        items(entries, key = { it.id }) { entry ->
            SavedGuestBookRow(item = entry, onClick = { onEntryClick(entry.id) })
            HorizontalDivider(thickness = 1.dp, color = RebornGridBorderGray)
        }
    }
}

@Preview
@Composable
private fun SavedScreenPostsPreview() {
    val previewState = SavedState(
        selectedTab = SavedTab.POSTS,
        posts = List(9) { index -> SavedGridItem(index.toString(), Res.drawable.img_memorial_bg_dummy) }
    )
    MaterialTheme {
        SavedScreen(state = previewState, onIntent = {})
    }
}

@Preview
@Composable
private fun SavedScreenGuestBookPreview() {
    val previewState = SavedState(
        selectedTab = SavedTab.GUESTBOOK,
        guestBookEntries = listOf(
            SavedGuestBookItem("1", "故 김영희 님의 메모리얼", "하늘에서도 평안하시길 바랍니다.", "2026.07.27 18:27", null)
        )
    )
    MaterialTheme {
        SavedScreen(state = previewState, onIntent = {})
    }
}
