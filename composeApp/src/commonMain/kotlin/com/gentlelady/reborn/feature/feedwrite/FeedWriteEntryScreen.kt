package com.gentlelady.reborn.feature.feedwrite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
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
import com.gentlelady.reborn.core.designsystem.components.SelectionCheckCircle
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.feedwrite.presentation.FeedPostType
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * "+" 버튼으로 진입하는 작성하기 바텀시트. 피드/리마인드 탭과 생전·사후 게시글 선택을 제공한다.
 * 리마인드 탭과 사후 게시글은 아직 화면이 없어 선택만 가능하고 "다음"은 생전 게시글일 때만 진행된다.
 */
@Composable
fun FeedWriteEntryScreen(
    onDismiss: () -> Unit,
    onNext: (FeedPostType) -> Unit
) {
    var selectedType by remember { mutableStateOf(FeedPostType.LIVING) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color.White)
                .clickable(enabled = false) {} // 시트 내부 클릭이 dismiss로 전파되지 않도록 차단
                .padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(RebornDividerGray)
            )

            Text(
                text = "작성하기",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 16.dp)
            )

            FeedWriteTabRow()

            HorizontalDivider(thickness = 1.dp, color = RebornDividerGray)

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                PostTypeOptionRow(
                    icon = Icons.Filled.Schedule,
                    title = "생전 게시글",
                    subtitle = "지금의 일상과 순간을 공유해 보세요.",
                    isSelected = selectedType == FeedPostType.LIVING,
                    onClick = { selectedType = FeedPostType.LIVING }
                )
                Spacer(modifier = Modifier.height(16.dp))
                PostTypeOptionRow(
                    icon = Icons.Filled.Schedule,
                    title = "사후 게시글",
                    subtitle = "내가 떠난 후 공개될 게시글을 남겨보세요.",
                    isSelected = selectedType == FeedPostType.POSTHUMOUS,
                    onClick = { selectedType = FeedPostType.POSTHUMOUS }
                )
            }

            Button(
                onClick = { onNext(selectedType) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RebornCobaltBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(52.dp)
            ) {
                Text(text = "다음", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun FeedWriteTabRow() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("피드", "리마인드")

    Row(modifier = Modifier.padding(horizontal = 20.dp)) {
        tabs.forEachIndexed { index, label ->
            val isSelected = index == selectedTabIndex
            Column(
                modifier = Modifier
                    .clickable { selectedTabIndex = index }
                    .padding(end = 20.dp, bottom = 12.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.Black else RebornSlateGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(28.dp)
                        .background(if (isSelected) RebornCobaltBlue else Color.Transparent)
                )
            }
        }
    }
}

@Composable
private fun PostTypeOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .then(
                if (isSelected) Modifier.background(RebornLightBlueBg) else Modifier
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(RebornLightBlueBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = RebornCobaltBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 12.sp, color = RebornSlateGray)
        }
        SelectionCheckCircle(isSelected = isSelected)
    }
}

@Preview
@Composable
private fun FeedWriteEntryScreenPreview() {
    MaterialTheme {
        FeedWriteEntryScreen(onDismiss = {}, onNext = {})
    }
}
