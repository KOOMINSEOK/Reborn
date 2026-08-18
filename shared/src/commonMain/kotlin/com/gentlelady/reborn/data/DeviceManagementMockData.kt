package com.gentlelady.reborn.data

import com.gentlelady.reborn.management.security.device_management.domain.model.LoggedInDeviceItem

object DeviceManagementMockData {

    val devices = listOf(
        LoggedInDeviceItem(
            id = "1",
            deviceName = "iPhone 14",
            activatedDateLabel = "활성화 날짜 2026년 7월 29일"
        )
    )
}
