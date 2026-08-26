package com.gentlelady.reborn.server.plugins

import java.sql.ResultSet
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

    private fun connection() =
        (dataSource ?: error("DB 미설정 — DB_URL 을 설정해야 이 엔드포인트를 쓸 수 있습니다")).connection

    /**
     * 단문 쿼리 실행 헬퍼. `?` 바인딩만 지원한다.
     * ponytail: 트랜잭션/배치 미지원. 여러 문장을 원자적으로 묶어야 하면 그때 withTransaction 추가.
     */
    fun <T> query(sql: String, vararg params: Any?, map: (ResultSet) -> T): List<T> =
        connection().use { conn ->
            conn.prepareStatement(sql).use { st ->
                params.forEachIndexed { i, p -> st.setObject(i + 1, p) }
                st.executeQuery().use { rs ->
                    buildList { while (rs.next()) add(map(rs)) }
                }
            }
        }

    fun <T> queryFirst(sql: String, vararg params: Any?, map: (ResultSet) -> T): T? =
        query(sql, *params, map = map).firstOrNull()

    fun update(sql: String, vararg params: Any?): Int =
        connection().use { conn ->
            conn.prepareStatement(sql).use { st ->
                params.forEachIndexed { i, p -> st.setObject(i + 1, p) }
                st.executeUpdate()
            }
        }
}
