// shared 모듈의 domain 레이어에 안착할 프로필 전용 모델 예시
package com.gentlelady.reborn.profile.domain.model

import org.jetbrains.compose.resources.DrawableResource

data class ProfileFeedItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val thumbnail: DrawableResource, // ◀ MemorialItem 사상과 일치하게 설계
    val likes: Int = 0,
    val comments: Int = 0
)