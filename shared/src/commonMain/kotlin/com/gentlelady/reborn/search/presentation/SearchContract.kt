package com.gentlelady.reborn.search.presentation

import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.search.domain.entity.MemorialSearchItem

/**
 * 검색 결과 정렬 기준.
 * 기본(검색 전) 화면에서는 노출되지 않고, 이름 검색을 실행했을 때만 정렬 버튼으로 노출된다.
 */
enum class SearchTab(val index: Int) {
    LIKES(0),      // 좋아요순
    VIEWS(1),      // 조회순
    FLOWERS(2)     // 조화많은순
}

// ========================================================================
// 1. 상태(State)
// ========================================================================
data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val currentTab: SearchTab = SearchTab.LIKES,

    // 검색 전 기본 화면: 생전/사후/메모리얼 히스토리 게시글 중 무작위 3개
    val randomFeed: List<HomePost> = emptyList(),
    // 이름 검색 결과 (currentTab 기준 정렬됨)
    val results: List<MemorialSearchItem> = emptyList(),

    val error: String? = null
) {
    /** 검색어가 있으면 정렬 버튼 + 결과 리스트, 없으면 무작위 피드 */
    val isSearching: Boolean get() = query.isNotBlank()
}

// ========================================================================
// 2. 인텐트(Intent)
// ========================================================================
sealed interface SearchIntent {
    data class UpdateQuery(val query: String) : SearchIntent
    data object ExecuteSearch : SearchIntent
    data class ChangeTab(val tab: SearchTab) : SearchIntent
    data class ClickResultItem(val itemId: String) : SearchIntent
}
