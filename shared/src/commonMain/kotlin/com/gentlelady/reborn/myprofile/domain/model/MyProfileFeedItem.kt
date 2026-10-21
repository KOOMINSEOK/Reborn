// shared 모듈의 domain 레이어에 안착할 마이프로필 전용 모델 예시
package com.gentlelady.reborn.myprofile.domain.model

import org.jetbrains.compose.resources.DrawableResource

data class MyProfileFeedItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val thumbnail: DrawableResource? = null, // 목업 이미지 (번들)
    val likes: Int = 0,
    val comments: Int = 0,
    /** 서버 글의 첫 사진. 있으면 [thumbnail] 보다 우선 */
    val imageUrl: String? = null,
)
