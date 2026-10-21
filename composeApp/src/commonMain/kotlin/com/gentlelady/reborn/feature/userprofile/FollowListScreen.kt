package com.gentlelady.reborn.feature.userprofile

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gentlelady.reborn.core.designsystem.components.RebornImage
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.userprofile.domain.FollowTab
import com.gentlelady.reborn.userprofile.domain.FollowUser
import com.gentlelady.reborn.userprofile.presentation.FollowListIntent
import com.gentlelady.reborn.userprofile.presentation.FollowListState
import com.gentlelady.reborn.userprofile.presentation.FollowListViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.compose.viewmodel.koinViewModel

/** 내비게이션 진입점 (`follows/{userId}/{tab}?handle=`). */
@Composable
fun FollowListRoute(
    userId: String,
    tab: FollowTab,
    handle: String?,
    onBack: () -> Unit,
    onUserClick: (String) -> Unit,
    viewModel: FollowListViewModel = koinViewModel(),
) {
    LaunchedEffect(userId) { viewModel.onIntent(FollowListIntent.Open(userId, tab)) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    FollowListScreen(state, handle, viewModel::onIntent, onBack, onUserClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowListScreen(
    state: FollowListState,
    handle: String?,
    onIntent: (FollowListIntent) -> Unit,
    onBack: () -> Unit,
    onUserClick: (String) -> Unit,
) {
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            onIntent(FollowListIntent.MessageShown)
        }
    }

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Text(handle?.let { "@$it" }.orEmpty(), fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
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
        Column(Modifier.fillMaxSize().padding(padding)) {
            val tabs = listOf(FollowTab.FOLLOWERS to "팔로워", FollowTab.FOLLOWING to "팔로잉")
            TabRow(
                selectedTabIndex = tabs.indexOfFirst { it.first == state.tab },
                containerColor = Color.White,
                contentColor = Color.Black,
                indicator = { positions ->
                    val index = tabs.indexOfFirst { it.first == state.tab }
                    with(TabRowDefaults) {
                        SecondaryIndicator(Modifier.tabIndicatorOffset(positions[index]), color = RebornCobaltBlue)
                    }
                }
            ) {
                tabs.forEach { (tab, label) ->
                    Tab(
                        selected = state.tab == tab,
                        onClick = { onIntent(FollowListIntent.SelectTab(tab)) },
                        text = {
                            Text(label, fontWeight = if (state.tab == tab) FontWeight.Bold else FontWeight.Normal)
                        }
                    )
                }
            }

            val page = state.current
            Box(Modifier.fillMaxSize()) {
                when {
                    page.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    page.loadFailed -> Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("목록을 불러오지 못했어요.", color = RebornSlateGray)
                        TextButton(onClick = { onIntent(FollowListIntent.Retry) }) { Text("다시 시도") }
                    }
                    page.loaded && page.users.isEmpty() -> Text(
                        if (state.tab == FollowTab.FOLLOWERS) "아직 팔로워가 없어요." else "아직 팔로우한 사람이 없어요.",
                        color = RebornSlateGray,
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    else -> UserList(
                        // 탭마다 스크롤 위치를 따로 — 탭을 바꿔도 각자 제자리
                        key = state.tab,
                        users = page.users,
                        hasMore = page.nextOffset != null,
                        isLoadingMore = page.isLoadingMore,
                        onLoadMore = { onIntent(FollowListIntent.LoadMore) },
                        onUserClick = onUserClick,
                        onFollowClick = { onIntent(FollowListIntent.ToggleFollow(it)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserList(
    key: FollowTab,
    users: List<FollowUser>,
    hasMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    onUserClick: (String) -> Unit,
    onFollowClick: (String) -> Unit,
) {
    val listState = key(key) { rememberLazyListState() }
    LaunchedEffect(listState, hasMore) {
        if (!hasMore) return@LaunchedEffect
        snapshotFlow { listState.layoutInfo.run { (visibleItemsInfo.lastOrNull()?.index ?: 0) >= totalItemsCount - 3 } }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadMore() }
    }
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        items(users, key = { it.id }) { user ->
            UserRow(user, onClick = { onUserClick(user.id) }, onFollowClick = { onFollowClick(user.id) })
        }
        if (isLoadingMore) {
            item {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            }
        }
    }
}

@Composable
private fun UserRow(user: FollowUser, onClick: () -> Unit, onFollowClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (user.avatarUrl != null) {
            RebornImage(url = user.avatarUrl, fallback = null, contentDescription = null,
                modifier = Modifier.size(44.dp).clip(CircleShape))
        } else {
            Box(Modifier.size(44.dp).clip(CircleShape).background(RebornDividerGray), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(user.displayName, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("@${user.handle}", fontSize = 12.sp, color = RebornSlateGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (!user.isMe) {
            Spacer(Modifier.width(8.dp))
            val modifier = Modifier.height(32.dp).widthIn(min = 76.dp)
            val shape = RoundedCornerShape(8.dp)
            val padding = PaddingValues(horizontal = 12.dp)
            if (user.followedByMe) {
                OutlinedButton(onClick = onFollowClick, modifier = modifier, shape = shape, contentPadding = padding,
                    border = BorderStroke(1.dp, RebornGridBorderGray)) {
                    Text("팔로잉", fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(onClick = onFollowClick, modifier = modifier, shape = shape, contentPadding = padding,
                    colors = ButtonDefaults.buttonColors(containerColor = RebornCobaltBlue)) {
                    Text("팔로우", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
