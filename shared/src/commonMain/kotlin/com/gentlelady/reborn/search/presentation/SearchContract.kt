package com.gentlelady.reborn.search.presentation

import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.search.domain.entity.MemorialSearchItem

/**
 * 탭 유형 정의 (0, 1: Post 피드 계열 / 2: 메모리얼 랭킹 계열)
 */
enum class SearchTab(val index: Int) {
    LIKES(0),      // 좋아요순
    VIEWS(1),      // 조회순
    FLOWERS(2)     // 조화많은순
}

// ========================================================================
// 1. 상태(State) 정의: UI가 그려야 할 독립 데이터 세트
// ========================================================================
data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val currentTab: SearchTab = SearchTab.LIKES, // Int 대신 안정적인 Enum 적용

    // 기획 변경 반영: 하나의 공통 SearchItem 리스트를 버리고, 탭에 맞는 전용 도메인 모델을 가짐
    val postResults: List<HomePost> = emptyList(),
    val memorialResults: List<MemorialSearchItem> = emptyList(),

    val error: String? = null
)

// ========================================================================
// 2. 인텐트(Intent) 정의: 유저의 모든 액션/의도 명세
// ========================================================================
sealed interface SearchIntent {
    data class UpdateQuery(val query: String) : SearchIntent
    data object ExecuteSearch : SearchIntent
    data class ChangeTab(val tab: SearchTab) : SearchIntent // Enum 기반 타겟팅으로 변경
    data class ClickResultItem(val itemId: String) : SearchIntent
}