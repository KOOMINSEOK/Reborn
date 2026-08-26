package com.gentlelady.reborn.server.plugins

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(val status: String, val db: String)
