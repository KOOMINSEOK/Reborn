package com.gentlelady.reborn.search.presentation

import androidx.lifecycle.ViewModel
import com.gentlelady.reborn.data.MockDataSource
import com.gentlelady.reborn.search.domain.entity.MemorialSearchItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SearchViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        SearchState(randomFeed = MockDataSource.searchDefaultFeed())
    )
    val state: StateFlow<SearchState> = _state.asStateFlow()

    fun dispatch(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.UpdateQuery -> _state.update {
                it.copy(query = intent.query, results = search(intent.query, it.currentTab))
            }
            is SearchIntent.ChangeTab -> _state.update {
                it.copy(currentTab = intent.tab, results = sort(it.results, intent.tab))
            }
            is SearchIntent.ExecuteSearch -> _state.update {
                it.copy(results = search(it.query, it.currentTab))
            }
            is SearchIntent.ClickResultItem -> Unit // 네비게이션은 composeApp 그래프에서 처리
        }
    }

    private fun search(query: String, tab: SearchTab): List<MemorialSearchItem> {
        val q = query.trim()
        if (q.isBlank()) return emptyList()
        val matched = MockDataSource.memorialSearchItems.filter { it.name.contains(q, ignoreCase = true) }
        return sort(matched, tab)
    }

    private fun sort(items: List<MemorialSearchItem>, tab: SearchTab): List<MemorialSearchItem> =
        when (tab) {
            SearchTab.LIKES -> items.sortedByDescending { it.likeValue }
            SearchTab.VIEWS -> items.sortedByDescending { it.viewValue }
            SearchTab.FLOWERS -> items.sortedByDescending { it.flowerValue }
        }
}
