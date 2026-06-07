package com.gentlelady.reborn.memorial_swipe.domain.model

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
    val albumImageUrl: String
)