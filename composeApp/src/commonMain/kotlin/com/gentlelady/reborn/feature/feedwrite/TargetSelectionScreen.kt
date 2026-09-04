package com.gentlelady.reborn.feature.feedwrite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.designsystem.components.CircleAvatarImage
import com.gentlelady.reborn.core.designsystem.components.SearchField
import com.gentlelady.reborn.core.designsystem.components.SelectionCheckCircle
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.feedwrite.presentation.FeedWriteIntent
import com.gentlelady.reborn.feedwrite.presentation.FeedWriteState
import com.gentlelady.reborn.feedwrite.presentation.MOCK_RECOMMENDED_FOLLOWERS
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetSelectionScreen(
    state: FeedWriteState,
    onIntent: (FeedWriteIntent) -> Unit,
    onBack: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    val followers = remember(query) {
        if (query.isBlank()) MOCK_RECOMMENDED_FOLLOWERS
        else MOCK_RECOMMENDED_FOLLOWERS.filter {
            it.name.contains(query, ignoreCase = true) || it.handle.contains(query, ignoreCase = true)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                title = { Text(text = "대상 선택", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = onDone) {
                        Text(text = "다음", color = RebornCobaltBlue, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0.dp)
            )
        },
        bottomBar = {
            Surface(color = Color.White) {
                Text(
                    text = "${state.selectedFollowerIds.size}명 선택됨",
                    fontSize = 13.sp,
                    color = RebornSlateGray,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            SearchField(
                query = query,
                onQueryChange = { query = it },
                placeholder = "팔로워 검색",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            Text(
                text = "추천 팔로워",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RebornSlateGray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(followers) { follower ->
                    val isSelected = follower.id in state.selectedFollowerIds
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onIntent(FeedWriteIntent.ToggleFollower(follower.id)) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircleAvatarImage(size = 44.dp, fallbackText = follower.name, borderWidth = 0.dp, shadowElevation = 0.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = follower.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(text = follower.handle, fontSize = 12.sp, color = RebornSlateGray)
                        }
                        SelectionCheckCircle(isSelected = isSelected)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TargetSelectionScreenPreview() {
    MaterialTheme {
        Surface {
            TargetSelectionScreen(state = FeedWriteState(), onIntent = {}, onBack = {}, onDone = {})
        }
    }
}
