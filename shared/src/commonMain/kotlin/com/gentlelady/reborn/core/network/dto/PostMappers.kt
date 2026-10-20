package com.gentlelady.reborn.core.network.dto

import com.gentlelady.reborn.core.network.ApiConfig
import com.gentlelady.reborn.home.domain.model.HomePost

/** 서버가 상대경로(`/static/...`)를 주면 BASE_URL 을 붙여 절대 URL 로. */
internal fun String?.toAbsoluteServerUrl(): String? = when {
    this == null -> null
    startsWith("http://") || startsWith("https://") -> this
    else -> ApiConfig.BASE_URL.trimEnd('/') + this
}

fun PostDto.toHomePost(): HomePost = HomePost(
    id = id,
    authorName = authorName,
    authorAvatarUrl = authorAvatarUrl.toAbsoluteServerUrl(),
    // ponytail: UI 가 아직 단일 이미지 — 첫 장만. 캐러셀 붙일 때 HomePost 를 목록으로.
    imageUrl = imageUrls.firstOrNull().toAbsoluteServerUrl(),
    caption = caption,
    isPosthumous = isPosthumous,
    isLocked = isPosthumous,
    likes = likeCount,
    comments = commentCount,
    postedAt = createdAt.take(10), // "2026-08-27T..." -> "2026-08-27"
)
