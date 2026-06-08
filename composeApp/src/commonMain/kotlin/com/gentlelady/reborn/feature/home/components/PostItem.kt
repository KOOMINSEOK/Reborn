package com.gentlelady.reborn.feature.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.gentlelady.reborn.home.domain.model.HomePost

@Composable
fun PostItem(post: HomePost) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        PostHeader(post)
        if (post.isPosthumous) {
            PosthumousBanner()
        }
        PostImageArea(post)
        PostActionRow(post)
        PostCaption(post)
    }
}

@Preview(name = "Normal Post Item", showBackground = true)
@Composable
fun NormalPostItemPreview() {
    val normalPost = HomePost(
        id = "1",
        authorName = "홍길동",
        authorProfileUrl = "",
        contentImageUrl = "",
        caption = "일반 상태의 게시물 미리보기 텍스트입니다.",
        postedAt = "",
        isPosthumous = false
    )
    MaterialTheme {
        Surface {
            PostItem(post = normalPost)
        }
    }
}

@Preview(name = "Posthumous Post Item", showBackground = true)
@Composable
fun PosthumousPostItemPreview() {
    val deadPost = HomePost(
        id = "2",
        authorName = "김첨지",
        authorProfileUrl = "",
        contentImageUrl = "",
        caption = "나의 마지막 기록이 여러분에게 닿기를...",
        postedAt = "",
        isPosthumous = true
    )
    MaterialTheme {
        Surface {
            PostItem(post = deadPost)
        }
    }
}