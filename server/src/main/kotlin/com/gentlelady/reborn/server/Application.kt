package com.gentlelady.reborn.server

import com.gentlelady.reborn.server.internal.PublishService
import com.gentlelady.reborn.server.plugins.configureDatabase
import com.gentlelady.reborn.server.plugins.configureMonitoring
import com.gentlelady.reborn.server.plugins.configureRouting
import com.gentlelady.reborn.server.plugins.configureSecurity
import com.gentlelady.reborn.server.plugins.configureSerialization
import com.gentlelady.reborn.server.seed.SeedRunner
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0") { module() }.start(wait = true)
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureDatabase()
    configureSecurity()
    configureRouting()
    SeedRunner.runIfEnabled(log)
    startPublishTicker()
}

/**
 * 프로덕션 예약발행은 Cloud Scheduler → POST /internal/publish-due.
 * 로컬 개발 편의로 PUBLISH_TICKER_SECONDS 가 있으면 인프로세스 타이머로도 돌린다.
 * ponytail: 인스턴스 여러 개면 티커는 끄고 Cloud Scheduler 만 (중복 실행 방지).
 */
private fun Application.startPublishTicker() {
    val seconds = Env.get("PUBLISH_TICKER_SECONDS")?.toLongOrNull() ?: return
    log.info("예약발행 인프로세스 티커 활성 (${seconds}s)")
    launch {
        while (isActive) {
            delay(seconds * 1000)
            runCatching { PublishService.publishDue() }
                .onSuccess { if (it > 0) log.info("예약발행: $it 건") }
                .onFailure { log.error("예약발행 티커 오류", it) }
        }
    }
}
