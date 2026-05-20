package com.gentlelady.reborn.feature.memorial_swipe

import androidx.compose.runtime.Immutable

@Immutable
data class MemorialSwipeState(
    val isLoading: Boolean = false,
    val memorialItems: List<MemorialItem> = emptyList(),
    val errorMessage: String? = null
)

data class MemorialItem(
    val id: String,
    val name: String,
    val jobTitle: String,
    val location: String,
    val birthDate: String,
    val deathDate: String,
    val profileImageUrl: String,
    val backgroundImageUrl: String,
    val currentMusic: MusicItem?
)

data class MusicItem(
    val title: String,
    val artist: String,
    val albumImageUrl: String // TopBar 컴포넌트의 변수명과 일치하도록 최적화
)