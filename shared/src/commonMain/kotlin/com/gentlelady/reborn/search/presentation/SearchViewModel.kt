package com.gentlelady.reborn.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.data.MockDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    init {
        // 프로토타입 초기 렌더링을 위해 기본 탭(LIKES)의 Mock 데이터 세팅
        loadMockData(SearchTab.LIKES)
    }

    /**
     * SearchScreen(UI)에서 발생한 유저 액션(Intent)을 받아 비즈니스 로직 처리
     */
    fun dispatch(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.UpdateQuery -> {
                _state.update { it.copy(query = intent.query) }
                // 필요 시 여기서 실시간 타이핑 쿼리 검색 로직 수행
            }
            is SearchIntent.ExecuteSearch -> {
                executeSearch()
            }
            is SearchIntent.ChangeTab -> {
                // 탭이 바뀔 때 상태를 변경하고, 해당 탭에 맞는 Mock 데이터를 로드
                _state.update { it.copy(currentTab = intent.tab) }
                loadMockData(intent.tab)
            }
            is SearchIntent.ClickResultItem -> {
                // 상세 페이지 네비게이션 트리거를 위한 SideEffect/Event 처리 영역
                // (현재는 화면 전환을 composeApp 그래프 단에서 낚아채도록 설계됨)
            }
        }
    }

    private fun executeSearch() {
        val currentQuery = _state.value.query
        if (currentQuery.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // 프로토타입 단계이므로 검색 쿼리가 포함된 결과만 가볍게 필터링하는 시뮬레이션
            when (_state.value.currentTab) {
                SearchTab.LIKES, SearchTab.VIEWS -> {
                    val filteredPosts = MockDataSource.homePosts.filter {
                        it.authorName.contains(currentQuery) || it.caption.contains(currentQuery)
                    }
                    _state.update { it.copy(isLoading = false, postResults = filteredPosts) }
                }
                SearchTab.FLOWERS -> {
                    val filteredMemorials = MockDataSource.memorialSearchItems.filter {
                        it.name.contains(currentQuery) || it.location.contains(currentQuery)
                    }
                    _state.update { it.copy(isLoading = false, memorialResults = filteredMemorials) }
                }
            }
        }
    }

    private fun loadMockData(tab: SearchTab) {
        // 기획서 시안(Screen 1, Screen 2)에 부합하도록 탭별 MockDataSource 세트 할당
        when (tab) {
            SearchTab.LIKES, SearchTab.VIEWS -> {
                _state.update {
                    it.copy(
                        postResults = MockDataSource.homePosts,
                        memorialResults = emptyList()
                    )
                }
            }
            SearchTab.FLOWERS -> {
                _state.update {
                    it.copy(
                        postResults = emptyList(),
                        memorialResults = MockDataSource.memorialSearchItems
                    )
                }
            }
        }
    }
}