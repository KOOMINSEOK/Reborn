package com.gentlelady.reborn.home.presentation.home

object HomeReducer {
    fun reduce(state: HomeState, result: HomeResult): HomeState {
        return when (result) {
            is HomeResult.Loading -> state.copy(isLoading = true)
            is HomeResult.FeedLoaded -> state.copy(isLoading = false, posts = result.posts)
            is HomeResult.Error -> state.copy(isLoading = false, error = result.message)
        }
    }
}