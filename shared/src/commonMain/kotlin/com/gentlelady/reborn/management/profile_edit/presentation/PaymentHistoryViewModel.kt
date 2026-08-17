package com.gentlelady.reborn.management.profile_edit.presentation

import androidx.lifecycle.ViewModel
import com.gentlelady.reborn.data.PaymentHistoryMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PaymentHistoryViewModel : ViewModel() {

    private val _state = MutableStateFlow(PaymentHistoryState())
    val state: StateFlow<PaymentHistoryState> = _state.asStateFlow()

    init {
        handleIntent(PaymentHistoryIntent.LoadPaymentHistory)
    }

    fun handleIntent(intent: PaymentHistoryIntent) {
        when (intent) {
            is PaymentHistoryIntent.LoadPaymentHistory -> loadMockData()
            is PaymentHistoryIntent.ClickBack -> { /* 상위 네비게이션에서 처리 */ }
            is PaymentHistoryIntent.ClickOrder -> { /* TODO: 주문 상세 화면 연동 */ }
        }
    }

    private fun loadMockData() {
        _state.update {
            it.copy(
                orders = PaymentHistoryMockData.orders,
                isLoading = false
            )
        }
    }
}
