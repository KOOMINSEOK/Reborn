package com.gentlelady.reborn.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource
import com.gentlelady.reborn.Res

import com.gentlelady.reborn.ic_home_memorial
import com.gentlelady.reborn.ic_home_notifications
import com.gentlelady.reborn.ic_lock
import com.gentlelady.reborn.ic_like
import com.gentlelady.reborn.ic_share
import com.gentlelady.reborn.ic_comment

// 중앙 관리 테마 컬러 임포트
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornBorderLightBlue
import com.gentlelady.reborn.core.theme.RebornDarkBlue
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornSoftBlue
import com.gentlelady.reborn.core.theme.RebornSlateGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit = {}
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text("Home", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                },
                actions = {
                    // 메모리얼 버튼
                    IconButton(
                        onClick = { /* Screen 2로 이동 로직 */ },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RebornLightBlueBg) // 중앙 컬러 적용
                            .border(width = 1.dp, color = RebornBorderLightBlue, shape = CircleShape) // 중앙 컬러 적용
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_home_memorial),
                            contentDescription = "Memorial Page",
                            tint = RebornCobaltBlue, // 중앙 컬러 적용
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // 알림 버튼
                    Box(
                        modifier = Modifier.size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { }) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_home_notifications),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .size(6.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-2).dp, y = 2.dp),
                            color = Color.Red,
                            shape = CircleShape
                        ) {}
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(state.posts) { post ->
                PostItem(post)
                Divider(color = RebornDividerGray, thickness = 1.dp) // 하드코딩 값 대체
            }
        }
    }
}

@Composable
fun PostItem(post: HomePost) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        // 1. 헤더 (프로필 + 이름 + 아이콘)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = Color.LightGray) {
                Icon(Icons.Default.Person, null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 15.sp)

            if (post.isPosthumous) {
                Spacer(modifier = Modifier.width(6.dp)) // 이름과의 간격을 살짝 조정
                Box(
                    modifier = Modifier
                        .size(24.dp) // 원형 배경의 전체 크기
                        .background(color = RebornSoftBlue, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_lock),
                        contentDescription = "Memorial Lock",
                        tint = RebornCobaltBlue,
                        modifier = Modifier.size(14.dp) // 원 안의 자물쇠 아이콘 크기
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = Color.Gray)
        }

        // 2. 사후 게시글 정보 박스 (Memorial Post일 때만 노출)
        if (post.isPosthumous) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(width = 1.dp, color = RebornBorderLightBlue, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(RebornLightBlueBg)
                    .padding(12.dp)
            ) {
                // ✅ verticalAlignment를 Top으로 변경하여 상단 정렬
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = RebornDarkBlue,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 1.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "사후 게시글 — 작성자가 미리 예약해 두고 사망 후 공개되는 게시글",
                        color = RebornDarkBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // 3. 메인 이미지 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(top = 4.dp)
                .background(RebornDividerGray) // 하드코딩 값 대체
        ) {
            Text("Image Area", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
        }

        // 4. 액션 아이콘들 (좋아요, 댓글, 공유)
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_like),
                contentDescription = "Like",
                tint = RebornSlateGray,
                modifier = Modifier.size(24.dp)
            )
            Text(" 128", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(Res.drawable.ic_comment),
                contentDescription = "Comment",
                tint = RebornSlateGray,
                modifier = Modifier.size(24.dp)
            )
            Text(" 34", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(Res.drawable.ic_share),
                contentDescription = "Share",
                tint = RebornSlateGray,
                modifier = Modifier.size(24.dp)
            )
        }

        // 5. 캡션 (유저네임 볼드 처리)
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("@${post.authorName} ")
                }
                append(post.caption)
            },
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val mockState = HomeState(
        posts = listOf(
            HomePost(id = "1", authorName = "홍길동", authorProfileUrl = "", contentImageUrl = "", caption = "설날을 맞아 북한산으로 등산을 다녀왔습니다. 여러분은 새해 목표로 어떤 목표를 세우셨나요?", postedAt = "", isPosthumous = false),
            HomePost(id = "2", authorName = "김첨지", authorProfileUrl = "", contentImageUrl = "", caption = "나의 마지막 기록이 여러분에게 닿기를...", postedAt = "", isPosthumous = true)
        )
    )
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen(state = mockState)
        }
    }
}