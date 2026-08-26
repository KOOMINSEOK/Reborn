package com.gentlelady.reborn.server.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.flywaydb.core.Flyway

/**
 * DB_URL 등 환경변수를 넣으면 Hikari 풀 + Flyway 마이그레이션이 붙는다.
 * 없으면 조용히 건너뛴다 (스캐폴드 단계).
 */
fun Application.configureDatabase() {
    val url = System.getenv("DB_URL")
    val user = System.getenv("DB_USER") ?: "postgres"
    val password = System.getenv("DB_PASSWORD").orEmpty()

    if (url.isNullOrBlank()) {
        log.warn(
            "DB_URL 미설정 — DB 없이 기동합니다. " +
                "Supabase 연결 문자열(jdbc:postgresql://...)을 DB_URL 로 넣으면 /health 가 connected 로 바뀝니다.",
        )
        return
    }

    val ds = HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = url
            username = user
            this.password = password
            maximumPoolSize = 5
            poolName = "reborn-server"
        },
    )

    // ponytail: DB 초기화 실패는 어떤 원인이든 서버를 죽이지 않는다 (fail-open).
    @Suppress("TooGenericExceptionCaught")
    try {
        Flyway.configure()
            .dataSource(ds)
            .locations("classpath:db/migration")
            .load()
            .migrate()
        Db.attach(ds)
        log.info("DB 연결 완료 · 마이그레이션 적용됨 (status=${Db.status()})")
    } catch (e: Exception) {
        log.error("DB 초기화 실패 — DB 없이 계속 기동합니다.", e)
        ds.close()
    }
}
