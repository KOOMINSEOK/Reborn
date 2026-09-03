package com.gentlelady.reborn.feature.message.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.message.presentation.GuestBookItem
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 방명록 검색 화면. 시안 '방명록 검색 활성 상태'.
 * 검색어 입력/삭제마다 실시간 필터: (고인 이름 포함) OR (방명록 내용 포함).
 * 정렬: 최신 기록(daysAgo 오름차순) 상단.
 */
@Composable
internal fun GuestBookSearchScreen(
    all: List<GuestBookItem>,
    onBack: () -> Unit,
    onResultClick: (GuestBookItem) -> Unit
) {
    var query by remember { mutableStateOf("") }

    val results = remember(query) {
        val q = query.trim()
        if (q.isBlank()) emptyList()
        else all.filter {
            it.deceasedName.contains(q, ignoreCase = true) ||
                it.recentContent.contains(q, ignoreCase = true)
        }.sortedBy { it.daysAgo }
    }

    Scaffold(containerColor = Color.White) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            SearchHeader(query = query, onQueryChange = { query = it }, onBack = onBack, placeholder = "메시지")

            if (query.isNotBlank()) {
                Text(
                    text = "검색 결과",
                    fontSize = 12.sp,
                    color = RebornSlateGray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            LazyColumn {
                items(results, key = { it.id }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onResultClick(item) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(44.dp).background(RebornDividerGray, CircleShape))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("故 ${item.deceasedName}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Spacer(Modifier.width(4.dp))
                                Text("🌸", fontSize = 12.sp)
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = highlight(item.recentContent, query.trim()),
                                fontSize = 13.sp,
                                color = RebornSlateGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(item.relativeTime, fontSize = 11.sp, color = RebornSlateGray)
                    }
                }
            }
        }
    }
}

/** 검색어와 일치하는 부분만 코발트 블루 볼드 처리 */
private fun highlight(text: String, query: String) = buildAnnotatedString {
    if (query.isBlank()) { append(text); return@buildAnnotatedString }
    var start = 0
    while (true) {
        val idx = text.indexOf(query, start, ignoreCase = true)
        if (idx < 0) { append(text.substring(start)); break }
        append(text.substring(start, idx))
        withStyle(SpanStyle(color = RebornCobaltBlue, fontWeight = FontWeight.Bold)) {
            append(text.substring(idx, idx + query.length))
        }
        start = idx + query.length
    }
}

@Preview
@Composable
private fun GuestBookSearchScreenPreview() {
    MaterialTheme {
        GuestBookSearchScreen(
            all = listOf(
                GuestBookItem("1", "이철수", "당신의 따뜻한 미소가 그립습니다...", "8개월 전", daysAgo = 240),
                GuestBookItem("2", "김미소", "그곳에서는 항상 행복하시길 바랄게요...", "1년 전", daysAgo = 400)
            ),
            onBack = {}, onResultClick = {}
        )
    }
}
