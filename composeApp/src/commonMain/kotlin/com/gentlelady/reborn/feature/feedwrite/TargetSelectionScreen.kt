package com.gentlelady.reborn.feature.feedwrite

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornGridIconGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetSelectionScreen(
    viewModel: FeedWriteViewModel,
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
                    text = "${viewModel.selectedFollowerIds.size}명 선택됨",
                    fontSize = 13.sp,
                    color = RebornSlateGray,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(RebornGridIconGray)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                androidx.compose.foundation.text.BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color.Black),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text(text = "팔로워 검색", fontSize = 14.sp, color = RebornSlateGray)
                        }
                        inner()
                    }
                )
            }

            Text(
                text = "추천 팔로워",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RebornSlateGray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(followers) { follower ->
                    val isSelected = follower.id in viewModel.selectedFollowerIds
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectedFollowerIds = if (isSelected) {
                                    viewModel.selectedFollowerIds - follower.id
                                } else {
                                    viewModel.selectedFollowerIds + follower.id
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape).background(RebornGridIconGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = RebornSlateGray)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = follower.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(text = follower.handle, fontSize = 12.sp, color = RebornSlateGray)
                        }
                        if (isSelected) {
                            Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "선택됨", tint = RebornCobaltBlue)
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(width = 1.5.dp, color = RebornDividerGray, shape = CircleShape)
                            )
                        }
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
            TargetSelectionScreen(viewModel = FeedWriteViewModel(), onBack = {}, onDone = {})
        }
    }
}
