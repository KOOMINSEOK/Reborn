package com.gentlelady.reborn.feature.userprofile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gentlelady.reborn.core.designsystem.PostCard.PostItem
import com.gentlelady.reborn.core.designsystem.comments.CommentCountEffect
import com.gentlelady.reborn.core.designsystem.comments.CommentSheet
import com.gentlelady.reborn.core.designsystem.components.RebornImage
import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.core.theme.RebornBackground
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDangerRed
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.userprofile.domain.FollowTab
import com.gentlelady.reborn.userprofile.domain.UserProfile
import com.gentlelady.reborn.userprofile.presentation.UserProfileIntent
import com.gentlelady.reborn.userprofile.presentation.UserProfileState
import com.gentlelady.reborn.userprofile.presentation.UserProfileViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.compose.viewmodel.koinViewModel

/** 내비게이션 진입점. 화면(내비게이션 항목)마다 ViewModel 하나. */
@Composable
fun UserProfileRoute(
    userId: String,
    onBack: () -> Unit,
    /** (그 사람 id, 탭, 핸들) */
    onOpenFollows: (String, FollowTab, String) -> Unit = { _, _, _ -> },
    /** 차단/해제가 서버에 반영됐을 때 (그 사람 id, 차단했나) — 홈 피드 등을 맞추게 */
    onBlockChanged: (String, Boolean) -> Unit = { _, _ -> },
    onUserClick: (String) -> Unit = {},
    /** 그 사람과 대화 (그 사람 id) */
    onMessage: (String) -> Unit = {},
    viewModel: UserProfileViewModel = koinViewModel(),
) {
    LaunchedEffect(userId) { viewModel.onIntent(UserProfileIntent.Load(userId)) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    CommentCountEffect { change ->
        if (change.target == InteractionTarget.POST) {
            viewModel.onIntent(UserProfileIntent.CommentCountChanged(change.parentId, change.delta))
        }
    }
    val token = state.blockChangedToken
    LaunchedEffect(token) {
        val change = state.lastBlockChange
        if (token > 0 && change != null) onBlockChanged(change.first, change.second)
    }
    UserProfileScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        onOpenFollows = onOpenFollows,
        onUserClick = onUserClick,
        onMessage = onMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    state: UserProfileState,
    onIntent: (UserProfileIntent) -> Unit,
    onBack: () -> Unit,
    onOpenFollows: (String, FollowTab, String) -> Unit = { _, _, _ -> },
    onUserClick: (String) -> Unit = {},
    onMessage: (String) -> Unit = {},
) {
    var commentPostId by remember { mutableStateOf<String?>(null) }
    var blockConfirm by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            onIntent(UserProfileIntent.MessageShown)
        }
    }

    Scaffold(
        containerColor = RebornBackground,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.profile?.handle?.let { "@$it" }.orEmpty(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    val profile = state.profile
                    if (profile != null && !profile.isMe) {
                        ProfileMenu(
                            blocked = profile.blockedByMe,
                            enabled = !state.isChangingBlock,
                            onBlockClick = { blockConfirm = true },
                            onUnblockClick = { onIntent(UserProfileIntent.SetBlocked(false)) }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            val profile = state.profile
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.loadFailed || profile == null -> Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("프로필을 불러오지 못했어요.", color = RebornSlateGray)
                    TextButton(onClick = { onIntent(UserProfileIntent.Retry) }) { Text("다시 시도") }
                }
                else -> ProfileContent(
                    state = state,
                    profile = profile,
                    onIntent = onIntent,
                    onCommentClick = { commentPostId = it },
                    onOpenFollows = { tab -> onOpenFollows(profile.id, tab, profile.handle) },
                    onMessage = { onMessage(profile.id) }
                )
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
                if (it != state.profile?.id) onUserClick(it) // 지금 보고 있는 사람이면 그대로
            }
        )
    }

    if (blockConfirm) {
        val name = state.profile?.displayName.orEmpty()
        AlertDialog(
            onDismissRequest = { blockConfirm = false },
            title = { Text("${name}님을 차단할까요?") },
            text = { Text("서로의 글과 프로필이 보이지 않고, 서로 팔로우도 끊어져요. 차단은 설정 > 차단한 계정에서 풀 수 있어요.") },
            confirmButton = {
                TextButton(onClick = {
                    blockConfirm = false
                    onIntent(UserProfileIntent.SetBlocked(true))
                }) { Text("차단", color = RebornDangerRed) }
            },
            dismissButton = { TextButton(onClick = { blockConfirm = false }) { Text("취소") } }
        )
    }
}

@Composable
private fun ProfileContent(
    state: UserProfileState,
    profile: UserProfile,
    onIntent: (UserProfileIntent) -> Unit,
    onCommentClick: (String) -> Unit,
    onOpenFollows: (FollowTab) -> Unit,
    onMessage: () -> Unit,
) {
    val listState = rememberLazyListState()
    // 끝에서 3개 안쪽까지 내려오면 다음 페이지
    LaunchedEffect(listState, state.nextOffset != null) {
        if (state.nextOffset == null) return@LaunchedEffect
        snapshotFlow { listState.layoutInfo.run { (visibleItemsInfo.lastOrNull()?.index ?: 0) >= totalItemsCount - 3 } }
            .distinctUntilChanged()
            .filter { it }
            .collect { onIntent(UserProfileIntent.LoadMore) }
    }

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        item(key = "header") {
            ProfileHeader(
                profile = profile,
                onFollowClick = { onIntent(UserProfileIntent.ToggleFollow) },
                onUnblockClick = { onIntent(UserProfileIntent.SetBlocked(false)) },
                unblockEnabled = !state.isChangingBlock,
                onMessageClick = onMessage,
                // 나를 차단했거나 비공개면 서버가 목록을 안 준다 → 잠긴 프로필은 숫자만
                onOpenFollows = if (profile.isLocked) null else onOpenFollows
            )
        }
        when {
            profile.blockedByMe -> item(key = "note") { Note("차단한 사용자예요. 차단을 풀면 글을 볼 수 있어요.") }
            profile.isLocked -> item(key = "note") { Note("비공개 계정이에요. 팔로우한 사람만 글을 볼 수 있어요.", locked = true) }
            state.posts.isEmpty() -> item(key = "note") { Note("아직 올린 글이 없어요.") }
            else -> items(state.posts, key = { it.id }) { post ->
                PostItem(
                    post = post,
                    onLikeClick = { onIntent(UserProfileIntent.ToggleLike(post.id)) },
                    onCommentClick = { onCommentClick(post.id) },
                    onSaveClick = { onIntent(UserProfileIntent.ToggleSave(post.id)) },
                    onArchiveClick = { onIntent(UserProfileIntent.ArchivePost(post.id)) }
                )
            }
        }
        if (state.loadMoreFailed) {
            item(key = "retry") { RetryRow { onIntent(UserProfileIntent.LoadMore) } }
        }
        if (state.isLoadingMore) {
            item(key = "more") {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    profile: UserProfile,
    onFollowClick: () -> Unit,
    onUnblockClick: () -> Unit,
    unblockEnabled: Boolean,
    onMessageClick: () -> Unit,
    onOpenFollows: ((FollowTab) -> Unit)?,
) {
    Column(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 24.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val shape = RoundedCornerShape(20.dp)
            if (profile.avatarUrl != null) {
                RebornImage(
                    url = profile.avatarUrl,
                    fallback = null,
                    contentDescription = "프로필 사진",
                    modifier = Modifier.size(80.dp).clip(shape).border(1.dp, RebornGridBorderGray, shape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    Modifier.size(80.dp).clip(shape).background(RebornDividerGray).border(1.dp, RebornGridBorderGray, shape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size(40.dp))
                }
            }
            Spacer(Modifier.width(20.dp))
            Metric(profile.followerCount, "팔로워", Modifier.weight(1f), onOpenFollows?.let { { it(FollowTab.FOLLOWERS) } })
            Metric(profile.followingCount, "팔로잉", Modifier.weight(1f), onOpenFollows?.let { { it(FollowTab.FOLLOWING) } })
        }

        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                profile.displayName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (profile.isPrivate) {
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.Lock, contentDescription = "비공개 계정", tint = RebornSlateGray, modifier = Modifier.size(14.dp))
            }
        }
        Text("@${profile.handle}", color = RebornSlateGray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (profile.bio.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(profile.bio, color = RebornSlateGray, fontSize = 13.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }

        if (!profile.isMe) {
            Spacer(Modifier.height(16.dp))
            if (profile.blockedByMe) {
                FollowOrUnblockButton(profile, onFollowClick, onUnblockClick, unblockEnabled, Modifier.fillMaxWidth())
            } else {
                // 팔로우 · 메시지 나란히
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FollowOrUnblockButton(profile, onFollowClick, onUnblockClick, unblockEnabled, Modifier.weight(1f))
                    OutlinedButton(
                        onClick = onMessageClick,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, RebornGridBorderGray)
                    ) { Text("메시지", color = Color.Black, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
private fun FollowOrUnblockButton(
    profile: UserProfile,
    onFollowClick: () -> Unit,
    onUnblockClick: () -> Unit,
    unblockEnabled: Boolean,
    modifier: Modifier,
) {
    val buttonModifier = modifier.height(40.dp)
    if (profile.blockedByMe) {
        // 차단 중엔 팔로우할 수 없다 → 버튼 자리에 해제
        OutlinedButton(
            onClick = onUnblockClick,
            enabled = unblockEnabled,
            modifier = buttonModifier,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, RebornGridBorderGray)
        ) { Text("차단 해제", color = Color.Black, fontWeight = FontWeight.Bold) }
    } else if (profile.followedByMe) {
        OutlinedButton(
            onClick = onFollowClick,
            modifier = buttonModifier,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, RebornGridBorderGray)
        ) { Text("팔로잉", color = Color.Black, fontWeight = FontWeight.Bold) }
    } else {
        Button(
            onClick = onFollowClick,
            modifier = buttonModifier,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RebornCobaltBlue)
        ) { Text("팔로우", fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun Metric(count: Int, label: String, modifier: Modifier, onClick: (() -> Unit)?) {
    Column(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(count.toString(), fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 12.sp, color = RebornSlateGray)
    }
}

@Composable
private fun Note(text: String, locked: Boolean = false) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (locked) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
        }
        Text(text, color = RebornSlateGray, fontSize = 13.sp, textAlign = TextAlign.Center)
    }
}

/** 프로필 오른쪽 위 ··· — 차단/해제 */
@Composable
private fun ProfileMenu(blocked: Boolean, enabled: Boolean, onBlockClick: () -> Unit, onUnblockClick: () -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { open = true }, enabled = enabled) {
            Icon(Icons.Default.MoreHoriz, contentDescription = "더보기")
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }, containerColor = Color.White) {
            DropdownMenuItem(
                text = { Text(if (blocked) "차단 해제" else "차단하기", color = if (blocked) Color.Black else RebornDangerRed) },
                onClick = {
                    open = false
                    if (blocked) onUnblockClick() else onBlockClick()
                }
            )
        }
    }
}
