package com.gentlelady.reborn.home.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
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

// 디자인에서 사용되는 Cobalt Blue 색상 정의
val CobaltBlue = Color(0xFFEFF4FF)
val BackgroundGray = Color(0xFFF8F9FA)
val BorderLightBlue = Color(0xFFB9D1FF)

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
                    // 1. 메모리얼 버튼 (36dp x 36dp)
                    IconButton(
                        onClick = { /* Screen 2로 이동 로직 */ },
                        modifier = Modifier
                            .size(36.dp) // 피그마 수치 36px 반영
                            .clip(CircleShape)
                            .background(CobaltBlue.copy(alpha = 0.1f))
                            .border(
                                width = 1.dp,
                                color = BorderLightBlue,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_home_memorial),
                            contentDescription = "Memorial Page",
                            tint = CobaltBlue,
                            modifier = Modifier.size(20.dp) // 원 안의 아이콘은 보통 20~24dp가 적당합니다.
                        )
                    }

                    // 2. 버튼 사이의 Gap (8px 반영)
                    Spacer(modifier = Modifier.width(8.dp))

                    // 3. 알림 버튼 (36dp x 36dp)
                    Box(
                        modifier = Modifier.size(36.dp), // 알림 버튼도 동일하게 36px
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_home_notifications),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // 알림 빨간 점 (위치 미세 조정)
                        Surface(
                            modifier = Modifier
                                .size(6.dp) // 점 크기는 디자인에 맞게 조절
                                .align(Alignment.TopEnd)
                                .offset(x = (-2).dp, y = 2.dp), // 버튼 안쪽으로 살짝 이동
                            color = Color.Red,
                            shape = CircleShape
                        ) {}
                    }

                    // 화면 가장 오른쪽 끝과의 여백 (필요시 추가)
                    Spacer(modifier = Modifier.width(20.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // 디자인 하단 네비게이션 구현
            NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Search, null) }, label = { Text("Search") })
                // 중앙 커스텀 플러스 버튼
                Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
                    FloatingActionButton(
                        onClick = {},
                        shape = CircleShape,
                        containerColor = CobaltBlue,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
                        modifier = Modifier.size(48.dp)
                    ) { Icon(Icons.Default.Add, null) }
                }
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Outlined.Email, null) }, label = { Text("Message") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Outlined.Person, null) }, label = { Text("Profile") })
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            items(state.posts) { post ->
                PostItem(post)
                Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
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
                // 여기에 프로필 이미지
                Icon(Icons.Default.Person, null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 15.sp)

            if (post.isPosthumous) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.LockOpen, contentDescription = null, tint = CobaltBlue, modifier = Modifier.size(16.dp))
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEEF4FF)) // 아주 연한 파랑
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, null, tint = CobaltBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "사후 게시글 — 작성자가 미리 예약해 두고 사망 후 공개되는 게시글",
                        color = CobaltBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
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
                .background(Color(0xFFEEEEEE))
        ) {
            // 실제 이미지가 없을 때를 위한 Placeholder
            Text("Image Area", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
        }

        // 4. 액션 아이콘들 (좋아요, 댓글, 공유)
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.FavoriteBorder, null, modifier = Modifier.size(24.dp))
            Text(" 128", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Outlined.ChatBubbleOutline, null, modifier = Modifier.size(24.dp))
            Text(" 34", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Outlined.Share, null, modifier = Modifier.size(24.dp))
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
        // ✅ Surface를 추가하여 기본적인 콘텐츠 컬러와 배경 환경을 제공합니다.
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen(state = mockState)
        }
    }
}