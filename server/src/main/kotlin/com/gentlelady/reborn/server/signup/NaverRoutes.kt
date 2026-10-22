package com.gentlelady.reborn.server.signup

import com.gentlelady.reborn.server.plugins.Db
import com.gentlelady.reborn.server.plugins.ErrorResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

@Serializable
data class NaverLoginRequest(val accessToken: String)

/** 네이버 사용자. id 는 우리 앱 전용 (네이버는 앱마다 다른 id 를 준다 — 다른 앱의 토큰으로는 우리 사용자가 될 수 없다) */
internal data class NaverUser(val id: String, val name: String?, val avatarUrl: String?)

/**
 * 네이버 로그인 → Supabase 세션. Supabase 가 네이버를 직접 지원하지 않아 서버가 잇는다.
 * 앱이 네이버 SDK 로 받은 access token 으로 네이버에 누군지 묻고 → 그 사람 계정(없으면 만듦)의 세션을 돌려준다.
 * 계정 이메일은 네이버 id 로 만든 가짜 주소(.invalid — 메일이 갈 수 없음). 네이버 이메일로 기존 계정에 붙이지 않는다
 * (네이버 이메일은 소유 확인이 안 된 연락처일 수 있어, 남의 이메일 계정에 들어가는 길이 된다).
 */
internal fun Route.naverRoutes(verify: (String) -> NaverUser? = ::fetchNaverUser) {
    post("/auth/naver") {
        val token = call.receive<NaverLoginRequest>().accessToken.trim()
        if (token.isEmpty()) return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("missing_token"))
        if (!SupabaseAdmin.configured) {
            return@post call.respond(HttpStatusCode.ServiceUnavailable, ErrorResponse("auth_not_configured"))
        }
        val naver = verify(token) ?: return@post call.respond(HttpStatusCode.Unauthorized, ErrorResponse("invalid_naver_token"))
        val email = naverEmail(naver.id)
        val created = SupabaseAdmin.createUser(
            buildJsonObject {
                put("email", email)
                put("email_confirm", true)
                // provider 는 세션을 낼 때 GoTrue 가 "email" 로 덮어써서 따로 둔다
                putJsonObject("app_metadata") {
                    put("reborn_provider", "naver")
                    putJsonArray("providers") { add(JsonPrimitive("naver")) }
                }
                putJsonObject("user_metadata") {
                    naver.name?.let { put("name", it) }
                    naver.avatarUrl?.let { put("avatar_url", it) }
                }
            },
        )
        // 이미 있던 주소면 정말 네이버로 만든 계정인지 확인 (누가 같은 주소로 먼저 만들어 둔 계정에 들어가지 않게)
        if (!created && !isNaverAccount(email)) {
            return@post call.respond(HttpStatusCode.Conflict, ErrorResponse("account_conflict"))
        }
        call.respondText(SupabaseAdmin.sessionFor(email), ContentType.Application.Json)
    }
}

internal fun naverEmail(naverId: String) =
    "naver_${hash("naver|$naverId").take(40)}@naver.reborn.invalid"

private fun isNaverAccount(email: String): Boolean =
    Db.queryFirst(
        "select coalesce(raw_app_meta_data ->> 'reborn_provider' = 'naver', false) as ok from auth.users where email = ?",
        email,
    ) { it.getBoolean("ok") } == true

private val naverHttp by lazy { HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build() }

/** 네이버 회원 프로필 조회로 토큰 확인. 틀린·만료 토큰이면 null. */
internal fun fetchNaverUser(token: String): NaverUser? {
    val req = HttpRequest.newBuilder(URI.create("https://openapi.naver.com/v1/nid/me"))
        .header("Authorization", "Bearer $token")
        .timeout(Duration.ofSeconds(10))
        .GET()
        .build()
    val res = naverHttp.send(req, HttpResponse.BodyHandlers.ofString())
    if (res.statusCode() == 401 || res.statusCode() == 403) return null
    check(res.statusCode() == 200) { "naver profile failed: ${res.statusCode()}" }
    val json = Json.parseToJsonElement(res.body())
    if (json.str("resultcode") != "00") return null
    val r = (json as? kotlinx.serialization.json.JsonObject)?.get("response") ?: return null
    val id = r.str("id")?.takeIf { it.isNotBlank() } ?: return null
    return NaverUser(id, r.str("name") ?: r.str("nickname"), r.str("profile_image"))
}
