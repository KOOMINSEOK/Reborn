package com.gentlelady.reborn.search.domain.entity

import org.jetbrains.compose.resources.DrawableResource

data class MemorialSearchItem(
    val id: String,
    val rank: Int,                       // 리스트 랭킹용
    val name: String,
    val birthDate: String,
    val deathDate: String,
    val location: String,
    val flowerCount: String,             // 리스트 스냅샷 정보 (표시용, 예: "24.8k")
    val profileImageUrl: DrawableResource?,
    val isVerified: Boolean = false,     // UI 스크린샷에 있던 블루 인증 마크 대응
    val isDeceased: Boolean = true,      // true면 이름 앞에 '故' 표시
    val hasProfile: Boolean = true,      // '프로필' 버튼 노출 여부
    val hasMemorial: Boolean = true,     // '메모리얼' 버튼 노출 여부
    // 정렬 기준값 (백엔드 연동 전까지 Mock 수치)
    val likeValue: Long = 0,             // 좋아요순: 프로필/메모리얼 게시글 좋아요 합
    val viewValue: Long = 0,             // 조회순: 프로필/메모리얼 조회수 합
    val flowerValue: Long = 0            // 조화많은순: 조화 수
)
