package com.gentlelady.reborn.memorial_swipe.presentation

sealed class MemorialSwipeIntent {
    object LoadData : MemorialSwipeIntent()
    object NavigateHome : MemorialSwipeIntent()
    data class FollowClick(val memorialId: String) : MemorialSwipeIntent()
    data class ShareClick(val memorialId: String) : MemorialSwipeIntent()
    data class VisitPageClick(val memorialId: String) : MemorialSwipeIntent()
}