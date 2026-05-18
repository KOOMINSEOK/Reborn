package com.gentlelady.reborn.feature.memorial_swipe

import androidx.compose.runtime.Immutable

// 1. 단일 데이터 구조 (UiState) 준수 및 재사용성 고려[cite: 25, 26, 35].
@Immutable
data class MemorialSwipeState(
    val isLoading: Boolean = false,
    val memorialItems: List<MemorialItem> = emptyList(),
    val errorMessage: String? = null
)

// 2. 도메인 특징: 추모 대상자의 전용 데이터 모델 정의[cite: 34].
data class MemorialItem(
    val id: String,
    val name: String, // 김첨지
    val jobTitle: String, // 소방관
    val location: String, // 서울특별시
    val birthDate: String, // 1987.03.02
    val deathDate: String, // 2024.01.03
    val profileImageUrl: String, // 프로필 이미지 URL
    val backgroundImageUrl: String, // 배경 호수 이미지 URL
    val currentMusic: MusicItem? // See You Again...
)

data class MusicItem(
    val title: String,
    val artist: String,
    val albumArtUrl: String
)