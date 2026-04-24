package com.gentlelady.reborn.home.presentation.home
import com.gentlelady.reborn.home.domain.model.HomePost

data class HomeState(
    val isLoading: Boolean = false,
    val posts: List<HomePost> = emptyList(),
    val error: String? = null
)

sealed interface HomeIntent {
    object LoadFeed : HomeIntent
    object ClickMemorialIcon : HomeIntent
}

sealed interface HomeResult {
    object Loading : HomeResult
    data class FeedLoaded(val posts: List<HomePost>) : HomeResult
    data class Error(val message: String) : HomeResult
}