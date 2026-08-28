package com.gentlelady.reborn.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.home.domain.usecase.GetHomeFeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomeFeedUseCase: GetHomeFeedUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadFeed()
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadFeed -> loadFeed()
            is HomeIntent.ClickMemorialIcon -> { /* 네비게이션은 UI 레이어에서 처리됨 */ }
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _state.update { HomeReducer.reduce(it, HomeResult.Loading) }
            // 서버 우선, 실패 시 레포지토리가 mock 으로 폴백한다.
            val posts = getHomeFeedUseCase()
            _state.update { HomeReducer.reduce(it, HomeResult.FeedLoaded(posts)) }
        }
    }
}
