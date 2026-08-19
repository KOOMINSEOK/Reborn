package com.gentlelady.reborn.management.security.domain.di

import com.gentlelady.reborn.management.security.account_visibility.presentation.AccountVisibilityViewModel
import com.gentlelady.reborn.management.security.blocked_accounts.presentation.BlockedAccountsViewModel
import com.gentlelady.reborn.management.security.change_password.presentation.ChangePasswordViewModel
import com.gentlelady.reborn.management.security.device_management.presentation.DeviceManagementViewModel
import org.koin.dsl.module

val securityModule = module {
    factory { AccountVisibilityViewModel() }
    factory { DeviceManagementViewModel() }
    factory { ChangePasswordViewModel() }
    factory { BlockedAccountsViewModel() }
}
