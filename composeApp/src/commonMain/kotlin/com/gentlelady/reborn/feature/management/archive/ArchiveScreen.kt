package com.gentlelady.reborn.feature.management.archive

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
        if (!state.isLoading && state.posts.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues)) {
                com.gentlelady.reborn.feature.management.saved.EmptyNote("보관한 게시물이 없어요.\n내 게시물 ··· 메뉴에서 보관할 수 있어요.")
            }
        } else {
            ImageGridAlbum(
                images = state.posts.map { com.gentlelady.reborn.feature.management.saved.gridSource(it.imageUrl, it.thumbnail) },
                onImageClick = { index -> onIntent(ArchiveIntent.ClickPost(state.posts[index].id)) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }

    state.restoreTarget?.let { post ->
        AlertDialog(
            onDismissRequest = { onIntent(ArchiveIntent.DismissRestore) },
            containerColor = Color.White,
            title = { Text("게시물을 되돌릴까요?") },
            text = { Text(post.caption.ifBlank { "보관한 게시물" } + "\n\n되돌리면 피드와 프로필에 다시 보여요.") },
            confirmButton = { TextButton(onClick = { onIntent(ArchiveIntent.ConfirmRestore) }) { Text("되돌리기") } },
            dismissButton = { TextButton(onClick = { onIntent(ArchiveIntent.DismissRestore) }) { Text("취소") } }
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
