package com.gentlelady.reborn.server

import java.io.File

/**
 * 아주 단순한 .env 로더. `KEY=VALUE`, `#` 주석, 값 양끝 따옴표만 처리한다.
 * 실제 환경변수(System.getenv)가 항상 우선한다.
 *
 * ponytail: multiline·`export ` 접두어·이스케이프 미지원. 필요해지면 dotenv-kotlin 도입.
 */
object Env {
    private val fromFile: Map<String, String> =
        listOf(File(".env"), File("server/.env"))
            .firstOrNull(File::exists)
            ?.let { parse(it.readText()) }
            .orEmpty()

    fun get(key: String): String? = System.getenv(key) ?: fromFile[key]

    fun require(key: String): String =
        get(key) ?: error("$key 미설정 — 환경변수 또는 server/.env 에 넣어주세요")

    fun parse(text: String): Map<String, String> = buildMap {
        for (raw in text.lines()) {
            val line = raw.trim()
            if (line.isEmpty() || line.startsWith("#") || "=" !in line) continue
            val key = line.substringBefore("=").trim()
            val value = line.substringAfter("=").trim().trim('"', '\'')
            if (key.isNotEmpty()) put(key, value)
        }
    }
}
