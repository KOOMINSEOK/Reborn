package com.gentlelady.reborn.management.app_settings.customer_support.faq.presentation

import androidx.lifecycle.ViewModel
import com.gentlelady.reborn.data.FaqMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FaqViewModel : ViewModel() {

    private val _state = MutableStateFlow(FaqState())
    val state: StateFlow<FaqState> = _state.asStateFlow()

    init {
        handleIntent(FaqIntent.LoadFaq)
    }

    fun handleIntent(intent: FaqIntent) {
        when (intent) {
            is FaqIntent.LoadFaq -> _state.update {
                it.copy(
                    items = FaqMockData.faqItems,
                    expandedIds = setOf("1", "4"),
                    isLoading = false
                )
            }
            is FaqIntent.ClickBack -> { /* 상위 네비게이션에서 처리 */ }
            is FaqIntent.ClickToggleFaq -> _state.update { s ->
                val expanded = if (intent.faqId in s.expandedIds) {
                    s.expandedIds - intent.faqId
                } else {
                    s.expandedIds + intent.faqId
                }
                s.copy(expandedIds = expanded)
            }
        }
    }
}
