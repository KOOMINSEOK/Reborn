package com.gentlelady.reborn.home.domain.model

import org.jetbrains.compose.resources.DrawableResource

data class HomePost(
    val id: String,
    val authorName: String,
    // 이미지: 서버 데이터는 URL(String), mock 은 로컬 리소스(DrawableResource). RebornImage 가 알아서 고른다.
    val authorProfileUrl: DrawableResource? = null,
    val authorAvatarUrl: String? = null,
    val contentImageUrl: DrawableResource? = null,
    val imageUrl: String? = null,
    val caption: String,
    val isPosthumous: Boolean = false,
    val isLocked: Boolean = false,
    val likes: Int = 0,
    val comments: Int = 0,
    val postedAt: String,
)
