package com.gentlelady.reborn.feature.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.img_memorial_bg_dummy
import com.gentlelady.reborn.img_memorial_profile_dummy
import com.gentlelady.reborn.profile.presentation.ProfileFeedItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ProfileFeedCarousel(
    feeds: List<ProfileFeedItem>,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        // 1. 섹션 헤더 타이틀 영역 (피드 - View All)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "피드",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "View All",
                color = RebornCobaltBlue, // 포인트 컬러 매칭 규칙 준수
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. 피드 캐로셀 영역 (HorizontalPager 활용 및 Peek 효과 설정)
        if (feeds.isEmpty()) {
            // 빈 상태 방어용 가상 컨테이너
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = 24.dp)
                    .background(RebornBackgroundGray, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("아직 작성된 피드가 없습니다.", color = RebornUnselectedGray, fontSize = 14.sp)
            }
        } else {
            val pagerState = rememberPagerState(pageCount = { feeds.size })

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                // contentPadding을 주어 다음 카드가 우측에 살짝 삐져나오도록 Peek 유도 (시안 저격)
                contentPadding = PaddingValues(start = 24.dp, end = 48.dp),
                pageSpacing = 16.dp
            ) { page ->
                CarouselCardItem(item = feeds[page])
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. 인디케이터 도트 영역 (시안 하단 블루 포인트 도트 묘사)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(feeds.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (isSelected) 12.dp else 6.dp, 6.dp) // 활성화 시 약간 길어지는 캡슐 형태
                            .background(
                                color = if (isSelected) RebornDeepBlue else RebornDividerGray,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun CarouselCardItem(
    item: ProfileFeedItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 상단 이미지 영역
            Image(
                painter = painterResource(item.thumbnail),
                contentDescription = "Feed Thumbnail",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            // 하단 텍스트 및 정보 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = item.title,
                        color = Color.Black,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.subtitle,
                        color = RebornSlateGray, // 슬레이트 그레이 세맨틱 폰트 컬러 매칭
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }

                // 좋아요 및 댓글 지표 메트릭 (하단 아이콘 행)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "♡  ${item.likes}",
                        color = RebornUnselectedGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "💬  ${item.comments}",
                        color = RebornUnselectedGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// --- 프리뷰 규칙 준수: PreviewParameterProvider 차단 및 Direct Injection 데이터 적용 ---
@Preview
@Composable
private fun ProfileFeedCarouselFullPreview() {
    // 1. 프리뷰 렌더러 안정성을 위해 함수 내부에서 100% 독립적인 가벼운 더미 리스트 직접 생성 (Direct Injection)
    val previewFeeds = listOf(
        ProfileFeedItem(
            id = "1",
            title = "가을 산책을 하며",
            subtitle = "예전에도 요즘에도 산책하는걸 좋아하는데요 오늘은 북한산으로 산책을 갔답니..",
            thumbnail = Res.drawable.img_memorial_bg_dummy, // 프로젝트에 안착된 기본 평평한 에셋 매칭
            likes = 24,
            comments = 6
        ),
        ProfileFeedItem(
            id = "2",
            title = "요즘 내가 좋아하는 것들",
            subtitle = "여러분들은 어떤 취미를 가지고 계신가요? 저는 최근에 독서와 음악 감상에..",
            thumbnail = Res.drawable.img_memorial_profile_dummy,
            likes = 42,
            comments = 11
        ),
        ProfileFeedItem(
            id = "3",
            title = "세 번째 기록",
            subtitle = "소중한 기억들을 여기에 차곡차곡 남겨둡니다. 나중에 꺼내볼 수 있도록..",
            thumbnail = Res.drawable.img_memorial_bg_dummy,
            likes = 15,
            comments = 2
        )
    )

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        ) {
            // 외부 의존성(ViewModel, MVI 파이프라인) 없이 순수하게 UI 레이아웃, 여백, 캡슐 도트 인디케이터 뼈대 검증
            ProfileFeedCarousel(
                feeds = previewFeeds,
                onViewAllClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun ProfileFeedCarouselEmptyPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        ) {
            // 데이터가 텅 비어있을 때 "아직 작성된 피드가 없습니다" 방어 문구가 시안대로 예쁘게 나오는지 검증
            ProfileFeedCarousel(
                feeds = emptyList(),
                onViewAllClick = {}
            )
        }
    }
}