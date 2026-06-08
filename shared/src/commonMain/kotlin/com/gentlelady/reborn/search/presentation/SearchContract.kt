package com.gentlelady.reborn.search.presentation

// 1. 상태(State) 정의: UI가 그려야 할 데이터
data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<SearchItem> = emptyList(),
    val currentTab: Int = 0 // 0: Post, 1: Memorial
)

// 2. 인텐트(Intent) 정의: 유저의 액션
sealed interface SearchIntent {
    data class UpdateQuery(val query: String) : SearchIntent
    object ExecuteSearch : SearchIntent
    data class ChangeTab(val tabIndex: Int) : SearchIntent
    data class ClickResultItem(val itemId: String) : SearchIntent
}

// 3. 더미 도메인 모델 (향후 Repository와 연결)
data class SearchItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: ItemType
)

enum class ItemType { POST, MEMORIAL }