package com.gentlelady.reborn.feature.memorial_swipe

// 1. MVI 패턴의 Intent 정의 준수[cite: 35].
sealed class MemorialSwipeIntent {
    object LoadData : MemorialSwipeIntent() // 데이터 로딩 시작
    object NavigateHome : MemorialSwipeIntent() // 상단 홈 아이콘 클릭
    data class FollowClick(val memorialId: String) : MemorialSwipeIntent() // 팔로우 버튼 클릭
    data class ShareClick(val memorialId: String) : MemorialSwipeIntent() // 공유하기 버튼 클릭
    data class VisitPageClick(val memorialId: String) : MemorialSwipeIntent() // 메모리얼 페이지 이동
}