package com.gentlelady.reborn.feature.message.components

import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gentlelady.reborn.core.designsystem.components.RebornImage
import com.gentlelady.reborn.message.presentation.UserSearchViewModel
import org.koin.compose.viewmodel.koinViewModel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.designsystem.components.SearchField
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.message.presentation.SuggestedUser
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 메시지 탭 검색 화면. 시안 '메시지 2'.
 * 상단 뒤로가기 + 검색필드, 아래 '추천 더보기' 목록 (X로 개별 제거).
 */
@Composable
internal fun MessageSuggestRoute(
    onBack: () -> Unit,
    onUserClick: (SuggestedUser) -> Unit,
    viewModel: UserSearchViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MessageSuggestScreen(
        query = state.query,
        users = state.users,
        onQueryChange = viewModel::onQueryChange,
        onBack = onBack,
        onUserClick = onUserClick
    )
}

/** 검색어가 없으면 추천(내가 팔로우하는 사람), 있으면 이름·아이디 검색 결과 */
@Composable
internal fun MessageSuggestScreen(
    query: String,
    users: List<SuggestedUser>,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onUserClick: (SuggestedUser) -> Unit
) {
    // X 로 지운 추천은 이 화면에서만 숨긴다
    var dismissed by remember { mutableStateOf(setOf<String>()) }
    val list = users.filterNot { it.id in dismissed }

    Scaffold(containerColor = Color.White) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            SearchHeader(query = query, onQueryChange = onQueryChange, onBack = onBack, placeholder = "검색")
            Spacer(Modifier.height(8.dp))
            if (query.isNotBlank() && list.isEmpty()) {
                Text(
                    "찾는 사람이 없어요.",
                    fontSize = 14.sp,
                    color = RebornSlateGray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            if (query.isBlank()) Text(
                text = "추천 더보기",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyColumn {
                items(list, key = { it.id }) { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUserClick(user) }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (user.avatarUrl.isNotBlank()) {
                            RebornImage(url = user.avatarUrl, fallback = null, contentDescription = null, modifier = Modifier.size(44.dp).clip(CircleShape))
                        } else {
                            Box(Modifier.size(44.dp).background(RebornDividerGray, CircleShape))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(user.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(user.handle, fontSize = 12.sp, color = RebornSlateGray)
                        }
                        if (query.isBlank()) IconButton(onClick = { dismissed = dismissed + user.id }) {
                            Icon(Icons.Default.Close, contentDescription = "삭제", tint = RebornSlateGray)
                        }
                    }
                }
            }
        }
    }
}

/** 뒤로가기 + 파란 테두리 검색필드. 메시지/방명록 검색 화면 공용 */
@Composable
internal fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    placeholder: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로", tint = Color.Black)
        }
        SearchField(
            query = query,
            onQueryChange = onQueryChange,
            placeholder = placeholder,
            backgroundColor = Color.White,
            border = BorderStroke(1.5.dp, RebornCobaltBlue),
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
    }
}

@Preview
@Composable
private fun MessageSuggestScreenPreview() {
    MaterialTheme {
        MessageSuggestScreen(
            query = "",
            users = listOf(
                SuggestedUser("1", "이준수", "lee_junnnn"),
                SuggestedUser("2", "하준표", "dlkfjd8skdjf2")
            ),
            onQueryChange = {}, onBack = {}, onUserClick = {}
        )
    }
}
