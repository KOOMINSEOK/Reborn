package com.gentlelady.reborn.core.network.dto

import com.gentlelady.reborn.home.domain.model.HomePost

fun PostDto.toHomePost(): HomePost = HomePost(
    id = id,
    authorName = authorName,
    authorAvatarUrl = authorAvatarUrl,
    imageUrl = imageUrl,
    caption = caption,
    isPosthumous = isPosthumous,
    isLocked = isPosthumous,
    likes = likeCount,
    comments = commentCount,
    postedAt = createdAt.take(10), // "2026-08-27T..." -> "2026-08-27"
)
