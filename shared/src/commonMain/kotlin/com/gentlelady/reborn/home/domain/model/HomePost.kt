package com.gentlelady.reborn.home.domain.model

data class HomePost(
    val id: String,
    val authorName: String,
    val authorProfileUrl: String,
    val contentImageUrl: String,
    val caption: String,
    val isPosthumous: Boolean = false, // 사후 게시글 여부 (디자인의 파란 박스)
    val isLocked: Boolean = false,      // 잠금 아이콘 여부
    val likes: Int = 0,
    val comments: Int = 0,
    val postedAt: String
)