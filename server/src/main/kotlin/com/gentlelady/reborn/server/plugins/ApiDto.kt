package com.gentlelady.reborn.server.plugins

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(val status: String, val db: String)

@Serializable
data class MeResponse(val userId: String, val email: String? = null)

@Serializable
data class ErrorResponse(val error: String)
