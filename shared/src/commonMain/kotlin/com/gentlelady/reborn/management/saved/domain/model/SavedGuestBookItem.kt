package com.gentlelady.reborn.management.saved.domain.model

import org.jetbrains.compose.resources.DrawableResource

/**
 * 다른 사람의 메모리얼에 남긴 방명록 중 내가 저장(스크랩)해 둔 기록 한 건.
 */
data class SavedGuestBookItem(
    val id: String,
    val memorialName: String, // 예: "故 김영희 님의 메모리얼"
    val message: String,
    val dateLabel: String, // 예: "2026.07.27 18:27"
    val authorAvatar: DrawableResource?
)
