package com.gentlelady.reborn.feature.management.archive

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.GridImageSource
import com.gentlelady.reborn.core.designsystem.components.ImageGridAlbum
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.img_memorial_bg_dummy
import com.gentlelady.reborn.management.archive.domain.model.ArchivedPostItem
import com.gentlelady.reborn.management.archive.presentation.ArchiveIntent
import com.gentlelady.reborn.management.archive.presentation.ArchiveState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ArchiveScreen(
    state: ArchiveState,
    onIntent: (ArchiveIntent) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(
                title = "게시물 보관함",
                onBackClick = { onIntent(ArchiveIntent.ClickBack) }
            )
        }
    ) { paddingValues ->
        ImageGridAlbum(
            images = state.posts.map { GridImageSource.Resource(it.thumbnail) },
            onImageClick = { index -> onIntent(ArchiveIntent.ClickPost(state.posts[index].id)) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Preview
@Composable
private fun ArchiveScreenPreview() {
    val previewState = ArchiveState(
        posts = List(9) { index -> ArchivedPostItem(index.toString(), Res.drawable.img_memorial_bg_dummy) }
    )
    MaterialTheme {
        ArchiveScreen(state = previewState, onIntent = {})
    }
}
