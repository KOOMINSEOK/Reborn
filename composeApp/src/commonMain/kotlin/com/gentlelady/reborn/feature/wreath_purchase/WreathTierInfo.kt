// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/wreath_purchase/WreathTierInfo.kt
package com.gentlelady.reborn.feature.wreath_purchase

import com.gentlelady.reborn.Res
import com.gentlelady.reborn.img_wreath_basic
import com.gentlelady.reborn.img_wreath_premium
import com.gentlelady.reborn.img_wreath_special
import org.jetbrains.compose.resources.DrawableResource

data class WreathTierInfo(
    val displayName: String,
    val price: String,
    val imageRes: DrawableResource
)

/** 등급 코드("basic"/"premium"/"special")로 주문 상품 표시에 필요한 이름/가격/이미지를 얻는다. */
fun wreathTierInfo(tier: String?): WreathTierInfo = when (tier) {
    "special" -> WreathTierInfo("온라인 화환 (스페셜형)", "₩4,900", Res.drawable.img_wreath_special)
    "premium" -> WreathTierInfo("온라인 화환 (프리미엄형)", "₩9,900", Res.drawable.img_wreath_premium)
    else -> WreathTierInfo("온라인 화환 (기본형)", "₩900", Res.drawable.img_wreath_basic)
}
