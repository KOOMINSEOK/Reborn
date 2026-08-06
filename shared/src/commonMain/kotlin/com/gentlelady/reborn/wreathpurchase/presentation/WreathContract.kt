// shared/src/commonMain/kotlin/com/gentlelady/reborn/wreathpurchase/presentation/WreathContract.kt
package com.gentlelady.reborn.wreathpurchase.presentation

data class WreathState(
    val selectedTier: String? = null,
    val selectedPaymentMethod: String = "card"
)

sealed interface WreathIntent {
    object ClickBack : WreathIntent
    data class SelectTier(val tier: String) : WreathIntent
    data class ClickBuy(val tier: String) : WreathIntent
    data class SelectPaymentMethod(val method: String) : WreathIntent
}
