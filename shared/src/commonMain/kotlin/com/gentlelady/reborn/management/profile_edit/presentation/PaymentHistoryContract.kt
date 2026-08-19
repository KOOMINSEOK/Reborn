package com.gentlelady.reborn.management.profile_edit.presentation

import com.gentlelady.reborn.management.profile_edit.domain.model.WreathOrderHistoryItem

data class PaymentHistoryState(
    val orders: List<WreathOrderHistoryItem> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface PaymentHistoryIntent {
    object LoadPaymentHistory : PaymentHistoryIntent
    object ClickBack : PaymentHistoryIntent
    data class ClickOrder(val orderId: String) : PaymentHistoryIntent
}
