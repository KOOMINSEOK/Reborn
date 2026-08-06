// shared/src/commonMain/kotlin/com/gentlelady/reborn/wreathpurchase/presentation/WreathViewModel.kt
package com.gentlelady.reborn.wreathpurchase.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WreathViewModel : ViewModel() {

    private val _state = MutableStateFlow(WreathState())
    val state: StateFlow<WreathState> = _state.asStateFlow()

    fun onIntent(intent: WreathIntent) {
        when (intent) {
            is WreathIntent.SelectTier -> {
                _state.update { it.copy(selectedTier = intent.tier) }
            }
            is WreathIntent.SelectPaymentMethod -> {
                _state.update { it.copy(selectedPaymentMethod = intent.method) }
            }
            is WreathIntent.ChangeMessageText -> {
                _state.update { it.copy(messageText = intent.text.take(100)) }
            }
            is WreathIntent.ChangeSenderName -> {
                _state.update { it.copy(senderName = intent.name) }
            }
            is WreathIntent.SelectFlowerDesign -> {
                _state.update { it.copy(selectedFlowerDesign = intent.design) }
            }
            // ClickBack / ClickBuy / ClickPay / ClickSubmitMessage는 NavGraph에서 가로채 네비게이션 처리
            else -> {}
        }
    }
}
