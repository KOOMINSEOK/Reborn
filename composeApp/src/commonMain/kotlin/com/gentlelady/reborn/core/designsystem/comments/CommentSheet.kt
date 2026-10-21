package com.gentlelady.reborn.core.designsystem.comments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDangerRed
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.interaction.domain.Comment
import com.gentlelady.reborn.interaction.presentation.CommentCountChange
import com.gentlelady.reborn.interaction.presentation.CommentsIntent
import com.gentlelady.reborn.interaction.presentation.CommentsViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * 글·추억 글 공용 댓글 시트. 열면 [parentId] 의 댓글을 처음부터 받는다.
 * 댓글 수 변화는 시트를 띄우는 화면이 [CommentCountEffect] 로 받는다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSheet(
    target: InteractionTarget,
    parentId: String,
    onDismiss: () -> Unit,
    /** 댓글 작성자를 눌렀을 때 (작성자 id). 시트는 띄운 쪽이 닫는다 */
    onAuthorClick: ((String) -> Unit)? = null,
    viewModel: CommentsViewModel = koinViewModel(),
) {
    LaunchedEffect(target, parentId) { viewModel.onIntent(CommentsIntent.Open(target, parentId)) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.75f).imePadding()) {
            Text(
                text = "댓글",
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            HorizontalDivider(color = RebornDividerGray)

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when {
                    state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    state.loadFailed -> CenterNote("댓글을 불러오지 못했어요.")
                    state.comments.isEmpty() -> CenterNote("아직 댓글이 없어요. 첫 댓글을 남겨 보세요.")
                    else -> CommentList(
                        comments = state.comments,
                        hasMore = state.nextOffset != null,
                        deletingIds = state.deletingIds,
                        onAuthorClick = onAuthorClick,
                        onLoadMore = { viewModel.onIntent(CommentsIntent.LoadMore) },
                        onDelete = { viewModel.onIntent(CommentsIntent.Delete(it)) }
                    )
                }
            }

            state.error?.let {
                Text(it, color = RebornDangerRed, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp))
            }
            HorizontalDivider(color = RebornDividerGray)
            CommentInput(
                text = state.input,
                isSending = state.isSending,
                onTextChange = { viewModel.onIntent(CommentsIntent.UpdateInput(it)) },
                onSend = { viewModel.onIntent(CommentsIntent.Send) }
            )
        }
    }
}

/**
 * 서버에 반영된 댓글 수 변화를 받는다 (시트를 닫은 뒤 끝난 작성·삭제 포함).
 * 시트를 띄우는 화면에 둔다 — 같은 화면(내비게이션 항목)의 [CommentsViewModel] 을 공유한다.
 */
@Composable
fun CommentCountEffect(
    viewModel: CommentsViewModel = koinViewModel(),
    onChange: (CommentCountChange) -> Unit,
) {
    val latest by rememberUpdatedState(onChange)
    LaunchedEffect(viewModel) { viewModel.countChanges.collect { latest(it) } }
}

@Composable
private fun BoxScope.CenterNote(text: String) {
    Text(text, color = RebornSlateGray, fontSize = 13.sp, modifier = Modifier.align(Alignment.Center))
}

@Composable
private fun CommentList(
    comments: List<Comment>,
    hasMore: Boolean,
    deletingIds: Set<String>,
    onAuthorClick: ((String) -> Unit)?,
    onLoadMore: () -> Unit,
    onDelete: (String) -> Unit,
) {
    val listState = rememberLazyListState()
    // 끝에서 3개 안쪽까지 내려오면 다음 페이지
    LaunchedEffect(listState, hasMore, comments.size) {
        if (!hasMore) return@LaunchedEffect
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .collect { last -> if (last >= comments.size - 3) onLoadMore() }
    }
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        items(comments, key = { it.id }) { comment ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            comment.authorName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .then(if (onAuthorClick != null) Modifier.clickable { onAuthorClick(comment.authorId) } else Modifier)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(comment.date, fontSize = 11.sp, color = RebornSlateGray)
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(comment.body, fontSize = 14.sp, lineHeight = 20.sp)
                }
                if (comment.isMine) {
                    TextButton(onClick = { onDelete(comment.id) }, enabled = comment.id !in deletingIds) {
                        Text("삭제", fontSize = 12.sp, color = RebornSlateGray)
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentInput(
    text: String,
    isSending: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    val canSend = text.isNotBlank() && !isSending
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp).navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("댓글 달기...", fontSize = 14.sp) },
            maxLines = 4,
            enabled = !isSending,
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() })
        )
        Spacer(Modifier.width(8.dp))
        TextButton(onClick = onSend, enabled = canSend) {
            Text("게시", color = if (canSend) RebornCobaltBlue else RebornSlateGray, fontWeight = FontWeight.Bold)
        }
    }
}
