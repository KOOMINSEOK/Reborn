package com.gentlelady.reborn.home.domain.model

import org.jetbrains.compose.resources.DrawableResource

data class HomePost(
    val id: String,
    val authorName: String,
    val authorProfileUrl: DrawableResource?, // KMP 로컬 리소스 대응을 위해 타입 변경 및 Nullable 처리
    val contentImageUrl: DrawableResource?, // KMP 로컬 리소스 대응을 위해 타입 변경 및 Nullable 처리
    val caption: String,
    val isPosthumous: Boolean = false,
    val isLocked: Boolean = false,
    val likes: Int = 0,
    val comments: Int = 0,
    val postedAt: String
)