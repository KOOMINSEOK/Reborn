package com.gentlelady.reborn.management.saved.domain.model

import org.jetbrains.compose.resources.DrawableResource

/**
 * "저장(스크랩)" 화면의 게시물/히스토리 탭이 공통으로 쓰는 3xN 그리드 셀 하나.
 * 두 탭 모두 이미지 한 장짜리 그리드라는 점이 동일해서 모델을 공유한다.
 */
data class SavedGridItem(
    val id: String,
    val thumbnail: DrawableResource
)
