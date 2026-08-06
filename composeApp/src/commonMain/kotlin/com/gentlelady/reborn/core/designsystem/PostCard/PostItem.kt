package com.gentlelady.reborn.core.designsystem.PostCard // 대문자 'C'로 정정

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.core.designsystem.components.ContentCard
import com.gentlelady.reborn.core.theme.RebornBackground
import com.gentlelady.reborn.home.domain.model.HomePost
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 조립되어 외부로 공개되는 메인 피드 카드 아이템. 공용 ContentCard 위에 홈 피드 전용
 * 액션 로우(PostActionRow)와 캡션(PostCaption)만 footer로 채워 넣는다.
 */
@Composable
fun PostItem(
    post: HomePost,
    modifier: Modifier = Modifier
) {
    ContentCard(
        authorName = post.authorName,
        subtitle = post.postedAt,
        authorProfileRes = post.authorProfileUrl,
        imageRes = post.contentImageUrl,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        headerBadge = if (post.isPosthumous) {
            { PosthumousLockBadge() }
        } else {
            null
        },
        bannerContent = if (post.isPosthumous) {
            { PosthumousBanner() }
        } else {
            null
        }
    ) {
        PostActionRow(post = post)
        PostCaption(post = post, modifier = Modifier.padding(bottom = 12.dp))
    }
}

// ========================================================================
// KMP Strict Rules 준수형 단독 프리뷰 그룹 (Direct Injection)
// ========================================================================

@Preview
@Composable
fun NormalPostItemPreview() {
    val normalPost = HomePost(
        id = "1",
        authorName = "홍길동",
        authorProfileUrl = null,
        contentImageUrl = null,
        caption = "일반 상태의 게시물 미리보기 텍스트입니다.",
        likes = 12,
        comments = 3,
        postedAt = "2026.02.18",
        isPosthumous = false
    )
    MaterialTheme {
        Surface(color = RebornBackground) {
            PostItem(post = normalPost)
        }
    }
}

@Preview
@Composable
fun PosthumousPostItemPreview() {
    val deadPost = HomePost(
        id = "2",
        authorName = "김첨지",
        authorProfileUrl = null,
        contentImageUrl = null,
        caption = "나의 마지막 기록이 여러분에게 닿기를...",
        likes = 150,
        comments = 45,
        postedAt = "2026.01.03",
        isPosthumous = true
    )
    MaterialTheme {
        Surface(color = RebornBackground) {
            PostItem(post = deadPost)
        }
    }
}