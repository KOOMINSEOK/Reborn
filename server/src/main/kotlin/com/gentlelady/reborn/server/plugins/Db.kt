package com.gentlelady.reborn.server.plugins

import javax.sql.DataSource

/**
 * 프로세스 전역 DB 핸들.
 *
 * 스캐폴드 단계 정책: DB 가 설정되지 않아도 서버는 정상 기동한다.
 * /health 가 상태만 보고한다 (not_configured / connected / error).
 */
object Db {
    @Volatile
    var dataSource: DataSource? = null
        private set

    fun attach(ds: DataSource) {
        dataSource = ds
    }

    /** 실시간 연결 확인. 미설정이면 false. */
    fun ping(): Boolean {
        val ds = dataSource ?: return false
        return runCatching {
            ds.connection.use { conn ->
                conn.createStatement().use { it.execute("select 1") }
            }
            true
        }.getOrDefault(false)
    }

    fun status(): String = when {
        dataSource == null -> "not_configured"
        ping() -> "connected"
        else -> "error"
    }
}
