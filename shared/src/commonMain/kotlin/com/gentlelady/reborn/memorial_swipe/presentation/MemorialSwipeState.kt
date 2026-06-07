package com.gentlelady.reborn.memorial_swipe.presentation

import com.gentlelady.reborn.memorial_swipe.domain.model.MemorialItem

data class MemorialSwipeState(
    val isLoading: Boolean = false,
    val memorialItems: List<MemorialItem> = emptyList(),
    val errorMessage: String? = null
)