package com.gentlelady.reborn.feature.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gentlelady.reborn.core.designsystem.comments.CommentSheet
import com.gentlelady.reborn.core.designsystem.components.RebornImage
import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.notification.Notification
import com.gentlelady.reborn.notification.NotificationType
import com.gentlelady.reborn.notification.NotificationsIntent
import com.gentlelady.reborn.notification.NotificationsState
import com.gentlelady.reborn.notification.NotificationsViewModel
import com.gentlelady.reborn.notification.relativeTime
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.compose.viewmodel.koinViewModel

/** 알림을 눌렀을 때 갈 곳. 화면 이동은 내비게이션이 한다. */
sealed interface NotificationTarget {
    data class User(val userId: String) : NotificationTarget
    data class Memorial(val memorialId: String, val focusGuestbookId: String? = null) : NotificationTarget
}

/** 내비게이션 진입점. 디자인이 나오기 전 임시 목록 (서버 연결은 그대로 두고 화면만 바꾸면 된다). */
@Composable
fun NotificationsRoute(
    onBack: () -> Unit,
    onOpen: (NotificationTarget) -> Unit,
    viewModel: NotificationsViewModel = koinViewModel(),
) {
    // 열 때마다(다른 화면에서 돌아올 때 포함) 새로 받고 읽음 처리
    LifecycleResumeEffect(Unit) {
        viewModel.onIntent(NotificationsIntent.Load)
        onPauseOrDispose { }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    NotificationsScreen(state, viewModel::onIntent, onBack, onOpen)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    state: NotificationsState,
    onIntent: (NotificationsIntent) -> Unit,
    onBack: () -> Unit,
    onOpen: (NotificationTarget) -> Unit,
) {
    // 게시글 댓글 알림은 그 글의 댓글을 바로 연다 (게시글 상세 화면이 아직 없다)
    var commentPostId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = { Text("알림", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.loadFailed -> Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("알림을 불러오지 못했어요.", color = RebornSlateGray)
                    TextButton(onClick = { onIntent(NotificationsIntent.Load) }) { Text("다시 시도") }
                }
                state.items.isEmpty() -> Text(
                    "아직 알림이 없어요.",
                    color = RebornSlateGray,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> NotificationList(state, onIntent) { n ->
                    when (n.type) {
                        NotificationType.FOLLOW, NotificationType.POST_LIKE -> onOpen(NotificationTarget.User(n.actorId))
                        NotificationType.POST_COMMENT -> n.postId?.let { commentPostId = it }
                        NotificationType.HISTORY_LIKE, NotificationType.HISTORY_COMMENT ->
                            n.memorialId?.let { onOpen(NotificationTarget.Memorial(it)) }
                        NotificationType.GUESTBOOK -> n.memorialId?.let { onOpen(NotificationTarget.Memorial(it, n.sourceId)) }
                    }
                }
            }
        }
    }

    commentPostId?.let { postId ->
        CommentSheet(
            target = InteractionTarget.POST,
            parentId = postId,
            onDismiss = { commentPostId = null },
            onAuthorClick = {
                commentPostId = null
                onOpen(NotificationTarget.User(it))
            }
        )
    }
}

@Composable
private fun NotificationList(
    state: NotificationsState,
    onIntent: (NotificationsIntent) -> Unit,
    onClick: (Notification) -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(listState, state.nextCursor != null) {
        if (state.nextCursor == null) return@LaunchedEffect
        snapshotFlow { listState.layoutInfo.run { (visibleItemsInfo.lastOrNull()?.index ?: 0) >= totalItemsCount - 3 } }
            .distinctUntilChanged()
            .filter { it }
            .collect { onIntent(NotificationsIntent.LoadMore) }
    }
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        items(state.items, key = { it.id }) { n -> NotificationRow(n, onClick = { onClick(n) }) }
        if (state.loadMoreFailed) {
            item {
                Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("더 불러오지 못했어요.", color = RebornSlateGray, fontSize = 13.sp)
                    TextButton(onClick = { onIntent(NotificationsIntent.LoadMore) }) { Text("다시 시도") }
                }
            }
        }
        if (state.isLoadingMore) {
            item {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(n: Notification, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            // 이번에 처음 본(안 읽었던) 알림은 옅게 강조
            .background(if (n.read) Color.White else RebornLightBlueBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (n.actorAvatarUrl != null) {
            RebornImage(url = n.actorAvatarUrl, fallback = null, contentDescription = null,
                modifier = Modifier.size(44.dp).clip(CircleShape))
        } else {
            Box(Modifier.size(44.dp).clip(CircleShape).background(RebornDividerGray), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(n.actorName) }
                    append(n.message)
                },
                fontSize = 14.sp,
                lineHeight = 20.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(relativeTime(n.createdAt), fontSize = 12.sp, color = RebornSlateGray)
        }
        if (n.imageUrl != null) {
            Spacer(Modifier.width(12.dp))
            RebornImage(url = n.imageUrl, fallback = null, contentDescription = null,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)))
        }
    }
}
