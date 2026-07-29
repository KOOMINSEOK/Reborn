package com.gentlelady.reborn.memorial_swipe.domain.model

import org.jetbrains.compose.resources.DrawableResource

data class MemorialItem(
    val id: String,
    val name: String,
    val jobTitle: String,
    val location: String,
    val birthDate: String,
    val deathDate: String,
    val profileImageUrl: DrawableResource,
    val backgroundImageUrl: DrawableResource,
    val currentMusic: MusicItem?,
    val rank: Int = 0,
    val flowerCount: String = "0"
)

data class MusicItem(
    val title: String,
    val artist: String,
    val albumImageUrl: String
)