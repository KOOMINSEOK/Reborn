package com.gentlelady.reborn.data

import com.gentlelady.reborn.management.profile_edit.domain.model.WreathOrderHistoryItem

object PaymentHistoryMockData {

    val orders = listOf(
        WreathOrderHistoryItem(
            id = "1",
            memorialName = "故 이철수 님의 메모리얼",
            wreathTypeLabel = "스페셜형 화환",
            paymentDateLabel = "2026.07.28 결제완료",
            priceLabel = "4,900원",
            wreathTierCode = "special"
        ),
        WreathOrderHistoryItem(
            id = "2",
            memorialName = "故 김영희 님의 메모리얼",
            wreathTypeLabel = "기본형 화환",
            paymentDateLabel = "2026.05.14 결제완료",
            priceLabel = "900원",
            wreathTierCode = "basic"
        ),
        WreathOrderHistoryItem(
            id = "3",
            memorialName = "故 박지훈 님의 메모리얼",
            wreathTypeLabel = "프리미엄형 화환",
            paymentDateLabel = "2025.11.02 결제완료",
            priceLabel = "9,900원",
            wreathTierCode = "premium"
        )
    )
}
