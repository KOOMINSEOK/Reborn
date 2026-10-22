package com.gentlelady.reborn.home.domain.model

import org.jetbrains.compose.resources.DrawableResource

data class HomePost(
    val id: String,
    /** 작성자 id (서버). 누르면 그 사람 프로필로. 목업 글은 null */
    val authorId: String? = null,
    val authorName: String,
    // 이미지: 서버 데이터는 URL(String), mock 은 로컬 리소스(DrawableResource). RebornImage 가 알아서 고른다.
    val authorProfileUrl: DrawableResource? = null,
    val authorAvatarUrl: String? = null,
    val contentImageUrl: DrawableResource? = null,
    /** 서버 이미지 URL 들(순서 = 표시 순서). 2장 이상이면 캐러셀. */
    val imageUrls: List<String> = emptyList(),
    val caption: String,
    val isPosthumous: Boolean = false,
    val isLocked: Boolean = false,
    val likes: Int = 0,
    /** 내가 좋아요를 눌렀는지 */
    val liked: Boolean = false,
    /** 내가 저장했나 */
    val saved: Boolean = false,
    /** 내 글인가 (··· 메뉴에 보관하기) */
    val mine: Boolean = false,
    val comments: Int = 0,
    val postedAt: String,
)
