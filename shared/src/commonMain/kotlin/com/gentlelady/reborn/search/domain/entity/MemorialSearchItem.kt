package com.gentlelady.reborn.search.domain.entity

import org.jetbrains.compose.resources.DrawableResource

data class MemorialSearchItem(
    val id: String,
    val rank: Int,                       // 리스트 랭킹용
    val name: String,
    val birthDate: String,
    val deathDate: String,
    val location: String,
    val flowerCount: String,             // 리스트 스냅샷 정보
    val profileImageUrl: DrawableResource?,
    val isVerified: Boolean = false      // UI 스크린샷에 있던 블루 인증 마크 대응
)