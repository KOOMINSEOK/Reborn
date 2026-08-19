package com.gentlelady.reborn.management.profile_edit.domain.model

/**
 * 결제 관리 화면에 노출되는 화환 주문 내역 한 건.
 */
data class WreathOrderHistoryItem(
    val id: String,
    val memorialName: String, // 예: "故 이철수 님의 메모리얼"
    val wreathTypeLabel: String, // 예: "스페셜형 화환"
    val paymentDateLabel: String, // 예: "2026.07.28 결제완료"
    val priceLabel: String, // 예: "4,900원"
    val wreathTierCode: String // "basic" / "premium" / "special" — composeApp의 wreathTierInfo()가 실제 이미지로 매핑
)
