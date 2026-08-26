package com.gentlelady.reborn.server

import com.gentlelady.reborn.server.plugins.configureDatabase
import com.gentlelady.reborn.server.plugins.configureMonitoring
import com.gentlelady.reborn.server.plugins.configureRouting
import com.gentlelady.reborn.server.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0") { module() }.start(wait = true)
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureDatabase()
    configureRouting()
}
