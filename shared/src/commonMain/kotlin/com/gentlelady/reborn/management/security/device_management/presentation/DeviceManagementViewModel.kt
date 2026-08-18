package com.gentlelady.reborn.management.security.device_management.presentation

import androidx.lifecycle.ViewModel
import com.gentlelady.reborn.data.DeviceManagementMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DeviceManagementViewModel : ViewModel() {

    private val _state = MutableStateFlow(DeviceManagementState())
    val state: StateFlow<DeviceManagementState> = _state.asStateFlow()

    init {
        handleIntent(DeviceManagementIntent.LoadDeviceManagement)
    }

    fun handleIntent(intent: DeviceManagementIntent) {
        when (intent) {
            is DeviceManagementIntent.LoadDeviceManagement -> _state.update {
                it.copy(devices = DeviceManagementMockData.devices, isLoading = false)
            }
            is DeviceManagementIntent.ClickBack -> { /* 상위 네비게이션에서 처리 */ }
            is DeviceManagementIntent.ClickCloseDeviceList -> { /* 상위 네비게이션에서 처리 */ }
            is DeviceManagementIntent.PasswordChanged -> _state.update {
                it.copy(password = intent.value, isPasswordError = false)
            }
            is DeviceManagementIntent.ClickVerifyPassword -> _state.update {
                if (it.password == MOCK_CORRECT_PASSWORD) {
                    it.copy(isVerified = true, isPasswordError = false)
                } else {
                    it.copy(isPasswordError = true)
                }
            }
        }
    }

    private companion object {
        // ponytail: 실제 인증 서버 연동 전까지 쓰는 목업 비밀번호
        const val MOCK_CORRECT_PASSWORD = "12345678"
    }
}
