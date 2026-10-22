package com.gentlelady.reborn.management.saved.domain.model

import org.jetbrains.compose.resources.DrawableResource

/**
 * "저장(스크랩)" 화면의 게시물/히스토리 탭이 공통으로 쓰는 3xN 그리드 셀 하나.
 * 두 탭 모두 이미지 한 장짜리 그리드라는 점이 동일해서 모델을 공유한다.
 */
data class SavedGridItem(
    val id: String,
    val thumbnail: DrawableResource? = null,
    /** 서버 사진 (첫 장). 사진 없는 글이면 null */
    val imageUrl: String? = null,
    /** 눌렀을 때 갈 곳: 게시물 = 글쓴이 id, 추억 글 = 추모 페이지 id */
    val ownerId: String? = null,
)
