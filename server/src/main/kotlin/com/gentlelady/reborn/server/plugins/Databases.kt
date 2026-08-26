package com.gentlelady.reborn.server.plugins

import com.gentlelady.reborn.server.Env
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.flywaydb.core.Flyway

private const val POOL_SIZE = 5

/**
 * DB_URL 등 환경변수를 넣으면 Hikari 풀 + Flyway 마이그레이션이 붙는다.
 * 없거나 초기화에 실패해도 서버는 계속 뜬다 (fail-open) — /health 가 상태를 보고한다.
 */
fun Application.configureDatabase() {
    val url = Env.get("DB_URL")
    val user = Env.get("DB_USER") ?: "postgres"
    val password = Env.get("DB_PASSWORD").orEmpty()

    if (url.isNullOrBlank()) {
        log.warn(
            "DB_URL 미설정 — DB 없이 기동합니다. " +
                "Supabase 연결 문자열(jdbc:postgresql://...)을 DB_URL 로 넣으면 /health 가 connected 로 바뀝니다.",
        )
        return
    }

    // ponytail: 드라이버/네트워크/URL 오류 등 어떤 원인이든 서버를 죽이지 않는다.
    @Suppress("TooGenericExceptionCaught")
    try {
        val ds = HikariDataSource(
            HikariConfig().apply {
                jdbcUrl = url
                username = user
                this.password = password
                maximumPoolSize = POOL_SIZE
                poolName = "reborn-server"
            },
        )
        Flyway.configure()
            .dataSource(ds)
            .locations("classpath:db/migration")
            .load()
            .migrate()
        Db.attach(ds)
        log.info("DB 연결 완료 · 마이그레이션 적용됨 (status=${Db.status()})")
    } catch (e: Exception) {
        log.error("DB 초기화 실패 — DB 없이 계속 기동합니다. (DB_URL 이 jdbc:postgresql:// 로 시작하는지 확인)", e)
    }
}
