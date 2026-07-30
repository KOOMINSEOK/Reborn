// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/memorial/history/MemorialHistoryDetailScreen.kt
package com.gentlelady.reborn.feature.memorial.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.CircleIconBadge
import com.gentlelady.reborn.core.designsystem.components.ContentCard
import com.gentlelady.reborn.core.theme.RebornBackgroundGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.data.MemorialMockData
import com.gentlelady.reborn.ic_bookmark
import com.gentlelady.reborn.ic_comment
import com.gentlelady.reborn.ic_like
import com.gentlelady.reborn.ic_share
import com.gentlelady.reborn.memorial.presentation.MemorialHistoryItem
import com.gentlelady.reborn.memorial.presentation.MemorialIntent
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialHistoryDetailScreen(
    item: MemorialHistoryItem,
    onIntent: (MemorialIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onIntent(MemorialIntent.ClickBack) }) {
                        CircleIconBadge(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            size = 36.dp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onIntent(MemorialIntent.ClickMore) }) {
                        CircleIconBadge(
                            icon = Icons.Filled.MoreHoriz,
                            contentDescription = "더보기",
                            size = 36.dp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 화면 타이틀 (탑바가 아니라 ContentCard 위쪽 콘텐츠 영역에 배치)
            Text(
                text = "히스토리(추억) 보기",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // 공용 ContentCard 위에 히스토리 전용 캡션 + 액션 로우만 footer로 채워 넣는다.
            ContentCard(
                authorName = item.authorName,
                subtitle = item.date,
                authorProfileRes = item.authorProfileRes,
                imageRes = item.imageRes,
                imageBitmap = item.imageBitmap,
                showMoreIcon = false,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // 1. 캡션 (히스토리는 "@이름" 접두어 없이 순수 본문만 표시, 회색 배경 박스 위에 표시)
                Text(
                    text = item.caption,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .background(color = RebornBackgroundGray, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.Black
                )

                // 2. 액션 로우 (좋아요, 댓글, 공유, 저장)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CountAction(iconRes = Res.drawable.ic_like, label = item.likes.toString(), contentDescription = "좋아요")
                    Spacer(modifier = Modifier.width(16.dp))
                    CountAction(iconRes = Res.drawable.ic_comment, label = item.comments.toString(), contentDescription = "댓글")
                    Spacer(modifier = Modifier.width(16.dp))
                    CountAction(iconRes = Res.drawable.ic_share, label = "공유", contentDescription = "공유")
                    Spacer(modifier = Modifier.weight(1f))
                    CountAction(iconRes = Res.drawable.ic_bookmark, label = "저장", contentDescription = "저장")
                }
            }
        }
    }
}

@Composable
private fun CountAction(
    iconRes: DrawableResource,
    label: String,
    contentDescription: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = RebornSlateGray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 13.sp, color = RebornSlateGray)
    }
}

@Preview
@Composable
private fun MemorialHistoryDetailScreenPreview() {
    MaterialTheme {
        Surface {
            MemorialHistoryDetailScreen(
                item = MemorialMockData.historyItems.first(),
                onIntent = {}
            )
        }
    }
}
