// shared/src/commonMain/kotlin/com/gentlelady/reborn/wreathpurchase/presentation/WreathContract.kt
package com.gentlelady.reborn.wreathpurchase.presentation

data class WreathState(
    val selectedTier: String? = null,
    val selectedPaymentMethod: String = "card",
    val messageText: String = "",
    val senderName: String = "",
    val selectedFlowerDesign: String = "lily"
)

sealed interface WreathIntent {
    object ClickBack : WreathIntent
    data class SelectTier(val tier: String) : WreathIntent
    data class ClickBuy(val tier: String) : WreathIntent
    data class SelectPaymentMethod(val method: String) : WreathIntent
    data class ClickPay(val tier: String) : WreathIntent
    data class ChangeMessageText(val text: String) : WreathIntent
    data class ChangeSenderName(val name: String) : WreathIntent
    data class SelectFlowerDesign(val design: String) : WreathIntent
    object ClickSubmitMessage : WreathIntent
}
