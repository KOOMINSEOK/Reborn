package com.gentlelady.reborn.management.security.blocked_accounts.domain.model

import org.jetbrains.compose.resources.DrawableResource

data class BlockedAccountItem(
    val id: String,
    val name: String,
    val username: String,
    val avatar: DrawableResource?
)
