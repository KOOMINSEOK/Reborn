// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/wreath_purchase/components/WreathTierInfo.kt
package com.gentlelady.reborn.feature.wreath_purchase.components

import com.gentlelady.reborn.Res
import com.gentlelady.reborn.img_wreath_basic
import com.gentlelady.reborn.img_wreath_premium
import com.gentlelady.reborn.img_wreath_special
import org.jetbrains.compose.resources.DrawableResource

data class WreathTierInfo(
    val displayName: String,
    val price: String,
    val imageRes: DrawableResource,
    /** 추모 메시지 작성 화면에서 이미지 아래에 보여주는 등급 요약 문구 */
    val messageSubtitle: String
)

/** 등급 코드("basic"/"premium"/"special")로 주문 상품 표시에 필요한 이름/가격/이미지를 얻는다. */
fun wreathTierInfo(tier: String?): WreathTierInfo = when (tier) {
    "special" -> WreathTierInfo(
        "온라인 화환 (스페셜형)", "₩4,900", Res.drawable.img_wreath_special,
        "스페셜형 화환 (꽃 디자인 선택 가능)"
    )
    "premium" -> WreathTierInfo(
        "온라인 화환 (프리미엄형)", "₩9,900", Res.drawable.img_wreath_premium,
        "프리미엄형 화환 (리본 색상 커스텀 가능)"
    )
    else -> WreathTierInfo(
        "온라인 화환 (기본형)", "₩900", Res.drawable.img_wreath_basic,
        "기본형 화환 (흑/백 리본 고정)"
    )
}
