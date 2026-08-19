package com.gentlelady.reborn.management.app_settings.customer_support.faq.presentation

import com.gentlelady.reborn.management.app_settings.customer_support.faq.domain.model.FaqItem

data class FaqState(
    val items: List<FaqItem> = emptyList(),
    // 펼쳐진 FAQ 항목 id 집합 (여러 개 동시에 펼칠 수 있다)
    val expandedIds: Set<String> = emptySet(),
    val isLoading: Boolean = true
)

sealed interface FaqIntent {
    object LoadFaq : FaqIntent
    object ClickBack : FaqIntent
    data class ClickToggleFaq(val faqId: String) : FaqIntent
}
