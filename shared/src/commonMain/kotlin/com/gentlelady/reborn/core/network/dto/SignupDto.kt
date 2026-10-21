package com.gentlelady.reborn.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignupStateDto(
    val termsAgreed: Boolean,
    val identityVerified: Boolean,
    val identityName: String? = null,
    val hasPassword: Boolean,
    val provider: String,
    val onboarded: Boolean,
)

@Serializable
data class TermsDto(val requiredAgreed: Boolean, val marketing: Boolean)

@Serializable
data class IdentityStartDto(
    val name: String,
    val birth: String,
    val genderDigit: String,
    val carrier: String,
    val phone: String,
)

@Serializable
data class IdentityStartResultDto(val requestId: String, val expiresAt: String, val devCode: String? = null)

@Serializable
data class IdentityVerifyDto(val requestId: String, val code: String)

@Serializable
data class IdentityVerifiedDto(val name: String)

@Serializable
data class ExistingAccountDto(val provider: String, val handle: String, val joinedAt: String)

@Serializable
data class AlreadyRegisteredDto(val error: String, val account: ExistingAccountDto)

@Serializable
data class HandleCheckDto(val available: Boolean, val reason: String? = null)

@Serializable
data class CompleteSignupDto(val handle: String, val purpose: String)
