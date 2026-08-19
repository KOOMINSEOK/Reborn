package com.gentlelady.reborn.management.security.device_management.domain.model

data class LoggedInDeviceItem(
    val id: String,
    val deviceName: String, // 예: "iPhone 14"
    val activatedDateLabel: String // 예: "활성화 날짜 2026년 7월 29일"
)
