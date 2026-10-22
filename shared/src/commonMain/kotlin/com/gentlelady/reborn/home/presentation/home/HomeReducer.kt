package com.gentlelady.reborn.home.presentation.home

import com.gentlelady.reborn.home.domain.model.HomePost

object HomeReducer {
    fun reduce(state: HomeState, result: HomeResult): HomeState {
        return when (result) {
            is HomeResult.Loading -> state.copy(isLoading = true)
            is HomeResult.FeedLoaded -> state.copy(isLoading = false, posts = result.posts)
            is HomeResult.Error -> state.copy(isLoading = false, error = result.message)
            is HomeResult.LikeChanged -> state.updatePost(result.postId) { it.copy(liked = result.liked, likes = result.likes) }
            is HomeResult.SaveChanged -> state.updatePost(result.postId) { it.copy(saved = result.saved) }
            is HomeResult.PostRemoved -> state.copy(posts = state.posts.filterNot { it.id == result.postId })
            is HomeResult.UnreadNotifications -> state.copy(unreadNotifications = result.count)
            is HomeResult.AuthorHidden -> state.copy(posts = state.posts.filterNot { it.authorId == result.authorId })
            is HomeResult.CommentCountChanged ->
                state.updatePost(result.postId) { it.copy(comments = (it.comments + result.delta).coerceAtLeast(0)) }
        }
    }
}

private fun HomeState.updatePost(id: String, change: (HomePost) -> HomePost) =
    copy(posts = posts.map { if (it.id == id) change(it) else it })
