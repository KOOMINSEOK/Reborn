package com.gentlelady.reborn.feature.userprofile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
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
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
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
    viewModel: UserProfileViewModel = koinViewModel(),
) {
    LaunchedEffect(userId) { viewModel.onIntent(UserProfileIntent.Load(userId)) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    CommentCountEffect { change ->
        if (change.target == InteractionTarget.POST) {
            viewModel.onIntent(UserProfileIntent.CommentCountChanged(change.parentId, change.delta))
        }
    }
    UserProfileScreen(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    state: UserProfileState,
    onIntent: (UserProfileIntent) -> Unit,
    onBack: () -> Unit,
) {
    var commentPostId by remember { mutableStateOf<String?>(null) }
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
                    onCommentClick = { commentPostId = it }
                )
            }
        }
    }

    commentPostId?.let { postId ->
        CommentSheet(target = InteractionTarget.POST, parentId = postId, onDismiss = { commentPostId = null })
    }
}

@Composable
private fun ProfileContent(
    state: UserProfileState,
    profile: UserProfile,
    onIntent: (UserProfileIntent) -> Unit,
    onCommentClick: (String) -> Unit,
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
            ProfileHeader(profile = profile, onFollowClick = { onIntent(UserProfileIntent.ToggleFollow) })
        }
        when {
            profile.blockedByMe -> item(key = "note") { Note("차단한 사용자예요. 차단을 풀면 글을 볼 수 있어요.") }
            profile.isLocked -> item(key = "note") { Note("비공개 계정이에요. 팔로우한 사람만 글을 볼 수 있어요.", locked = true) }
            state.posts.isEmpty() -> item(key = "note") { Note("아직 올린 글이 없어요.") }
            else -> items(state.posts, key = { it.id }) { post ->
                PostItem(
                    post = post,
                    onLikeClick = { onIntent(UserProfileIntent.ToggleLike(post.id)) },
                    onCommentClick = { onCommentClick(post.id) }
                )
            }
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
private fun ProfileHeader(profile: UserProfile, onFollowClick: () -> Unit) {
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
            Metric(profile.followerCount, "팔로워", Modifier.weight(1f))
            Metric(profile.followingCount, "팔로잉", Modifier.weight(1f))
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
            val buttonModifier = Modifier.fillMaxWidth().height(40.dp)
            if (profile.followedByMe) {
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
    }
}

@Composable
private fun Metric(count: Int, label: String, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
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
