package com.gentlelady.reborn.management.security.device_management.presentation

import com.gentlelady.reborn.management.security.device_management.domain.model.LoggedInDeviceItem

data class DeviceManagementState(
    val password: String = "",
    val isPasswordError: Boolean = false,
    val isVerified: Boolean = false,
    val devices: List<LoggedInDeviceItem> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface DeviceManagementIntent {
    object LoadDeviceManagement : DeviceManagementIntent
    object ClickBack : DeviceManagementIntent
    object ClickCloseDeviceList : DeviceManagementIntent
    data class PasswordChanged(val value: String) : DeviceManagementIntent
    object ClickVerifyPassword : DeviceManagementIntent
}
