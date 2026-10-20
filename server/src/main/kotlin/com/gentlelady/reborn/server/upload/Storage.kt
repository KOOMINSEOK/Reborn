package com.gentlelady.reborn.server.upload

import com.gentlelady.reborn.server.Env
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.UUID

/**
 * Supabase Storage `images` 버킷 (V9). 앱은 서버가 발급한 서명 URL 로 직접 PUT 한다 — 파일이 서버를 거치지 않음.
 * 크기(10MB)·형식(jpeg/png/webp) 제한은 버킷 설정이 강제한다.
 */
object Storage {
    const val BUCKET = "images"

    private val supabaseUrl = Env.get("SUPABASE_URL")?.takeIf { it.isNotBlank() }?.trimEnd('/')
    private val secretKey = Env.get("SUPABASE_SECRET_KEY")?.takeIf { it.isNotBlank() }
    private val http by lazy { HttpClient.newBuilder().connectTimeout(TIMEOUT).build() }

    val configured: Boolean get() = supabaseUrl != null && secretKey != null

    fun publicUrl(path: String): String = "$supabaseUrl/storage/v1/object/public/$BUCKET/$path"

    /** 이 사용자가 [Storage] 로 올린 이미지 URL 인가. null(이미지 없음)은 허용. */
    fun isOwnImage(userId: UUID, url: String?): Boolean =
        url == null || (supabaseUrl != null && isOwnImageUrl(supabaseUrl, userId, url))

    /**
     * `{userId}/{uuid}.{ext}` 경로에 쓸 수 있는 서명 업로드 URL (Supabase 기본 2시간 유효).
     * ponytail: 사용자별 발급 횟수 제한 없음 (파일당 10MB 는 버킷이 막음). 남용 보이면 고아 파일 정리와 함께 rate limit 추가.
     */
    fun signUpload(path: String): String {
        val req = HttpRequest.newBuilder(URI("$supabaseUrl/storage/v1/object/upload/sign/$BUCKET/$path"))
            // sb_secret_ 키는 JWT 가 아니라 apikey 헤더로만 보낸다 (Supabase 신규 키 규약)
            .header("apikey", secretKey)
            .header("Content-Type", "application/json")
            .timeout(TIMEOUT)
            .POST(HttpRequest.BodyPublishers.ofString("{}"))
            .build()
        val res = http.send(req, HttpResponse.BodyHandlers.ofString())
        check(res.statusCode() == 200) { "storage sign failed: ${res.statusCode()} ${res.body()}" }
        // 응답 url 은 `/object/upload/sign/...?token=...` 형태의 상대 경로
        val relative = Json.parseToJsonElement(res.body()).jsonObject.getValue("url").jsonPrimitive.content
        return "$supabaseUrl/storage/v1$relative"
    }
}

private val TIMEOUT: Duration = Duration.ofSeconds(10)

/** 확장자 → 서버가 허용하는 content type. 버킷 allowed_mime_types 와 같게 유지. */
val IMAGE_EXTENSIONS = mapOf("image/jpeg" to "jpg", "image/png" to "png", "image/webp" to "webp")

/** `{supabaseUrl}/storage/v1/object/public/images/{userId}/{uuid}.{jpg|png|webp}` 와 정확히 일치해야 한다. */
internal fun isOwnImageUrl(supabaseUrl: String, userId: UUID, url: String): Boolean {
    val prefix = "$supabaseUrl/storage/v1/object/public/${Storage.BUCKET}/$userId/"
    val exts = IMAGE_EXTENSIONS.values.joinToString("|")
    return Regex(Regex.escape(prefix) + "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.($exts)")
        .matches(url)
}
