package com.gentlelady.reborn.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.data.MockDataSource // 1. Mock 데이터 가져오기
import com.gentlelady.reborn.home.domain.usecase.GetHomeFeedUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomeFeedUseCase: GetHomeFeedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        // 2. ViewModel이 생성되자마자 즉시 데이터 로드
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

            // 3. 프로토타입 단계에서는 Mock 데이터를 사용
            val posts = MockDataSource.homePosts

            // 실제 서버 연동 시에는 아래 주석처럼 사용
            // val posts = getHomeFeedUseCase()

            _state.update { HomeReducer.reduce(it, HomeResult.FeedLoaded(posts)) }
        }
    }
}