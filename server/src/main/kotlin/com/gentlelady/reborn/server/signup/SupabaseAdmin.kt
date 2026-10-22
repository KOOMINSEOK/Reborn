package com.gentlelady.reborn.server.signup

import com.gentlelady.reborn.server.Env
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.UUID

/** Supabase 관리자 API (서버 비밀키). 빈 계정 지우기·네이버 계정 만들고 세션 내주기에만 쓴다. */
internal object SupabaseAdmin {
    private val url = Env.get("SUPABASE_URL")?.trimEnd('/')
    private val key = Env.get("SUPABASE_SECRET_KEY")?.takeIf { it.isNotBlank() }
    private val http by lazy { HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build() }

    val configured get() = url != null && key != null

    fun deleteUser(id: UUID): Boolean =
        runCatching { send("DELETE", "/auth/v1/admin/users/$id").statusCode() in 200..299 }.getOrDefault(false)

    /** 계정을 만든다. 그 이메일 계정이 이미 있으면 false. */
    fun createUser(body: JsonObject): Boolean {
        val res = send("POST", "/auth/v1/admin/users", body.toString())
        if (res.statusCode() == 422) return false
        check(res.statusCode() in 200..299) { "create user failed: ${res.statusCode()}" }
        return true
    }

    /**
     * 그 이메일 계정의 새 세션 (메일은 안 보낸다 — 관리자 링크를 서버가 바로 확인).
     * 응답은 GoTrue 세션 JSON 그대로 (access_token·refresh_token·expires_in·user).
     */
    fun sessionFor(email: String): String {
        val link = send("POST", "/auth/v1/admin/generate_link", buildJsonObject {
            put("type", "magiclink")
            put("email", email)
        }.toString())
        check(link.statusCode() in 200..299) { "generate link failed: ${link.statusCode()}" }
        val json = Json.parseToJsonElement(link.body()).jsonObject
        val hash = json.str("hashed_token") ?: json["properties"]?.str("hashed_token") ?: error("no hashed_token")
        val verify = send("POST", "/auth/v1/verify", buildJsonObject {
            put("type", "magiclink")
            put("token_hash", hash)
        }.toString())
        check(verify.statusCode() in 200..299) { "verify failed: ${verify.statusCode()}" }
        return verify.body()
    }

    private fun send(method: String, path: String, body: String? = null): HttpResponse<String> {
        val req = HttpRequest.newBuilder(URI.create("${url ?: error("SUPABASE_URL 미설정")}$path"))
            .header("apikey", key ?: error("SUPABASE_SECRET_KEY 미설정"))
            .header("Authorization", "Bearer $key")
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(10))
            .method(method, body?.let(HttpRequest.BodyPublishers::ofString) ?: HttpRequest.BodyPublishers.noBody())
            .build()
        return http.send(req, HttpResponse.BodyHandlers.ofString())
    }
}

/** JSON 객체의 문자열 필드 (없거나 문자열이 아니면 null) */
internal fun JsonElement.str(name: String): String? =
    ((this as? JsonObject)?.get(name) as? JsonPrimitive)?.takeIf { it.isString }?.content
