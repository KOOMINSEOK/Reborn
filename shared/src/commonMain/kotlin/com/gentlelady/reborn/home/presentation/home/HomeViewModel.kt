package com.gentlelady.reborn.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.home.domain.usecase.GetHomeFeedUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomeFeedUseCase: GetHomeFeedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadFeed -> loadFeed()
            is HomeIntent.ClickMemorialIcon -> { /* 스크린 전환 로직은 나중에 */ }
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _state.update { HomeReducer.reduce(it, HomeResult.Loading) }
            // UseCase를 통해 데이터를 가져와서 Reducer에 전달
            val posts = getHomeFeedUseCase()
            _state.update { HomeReducer.reduce(it, HomeResult.FeedLoaded(posts)) }
        }
    }
}