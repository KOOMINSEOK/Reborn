// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/wreath_purchase/components/WreathFlowerDesign.kt
package com.gentlelady.reborn.feature.wreath_purchase.components

import androidx.compose.ui.graphics.Color
import com.gentlelady.reborn.core.theme.RebornCobaltBlue

data class WreathFlowerDesign(
    val id: String,
    val label: String,
    val accentColor: Color
)

/** 스페셜형 화환 전용 꽃 디자인 옵션(1. 꽃 디자인 선택 단계에서 사용) */
val wreathFlowerDesigns = listOf(
    WreathFlowerDesign("lily", "단아한 백합", RebornCobaltBlue),
    WreathFlowerDesign("yellow_mix", "옐로우 믹스", Color(0xFFF5A524)),
    WreathFlowerDesign("premium_orchid", "프리미엄 호접란", Color(0xFF9333EA))
)

fun wreathFlowerDesignLabel(id: String): String =
    wreathFlowerDesigns.firstOrNull { it.id == id }?.label ?: wreathFlowerDesigns.first().label
