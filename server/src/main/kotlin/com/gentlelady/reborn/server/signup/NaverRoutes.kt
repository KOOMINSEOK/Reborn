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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
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

/**
 * 네이버 사용자. id 는 앱마다 다르다 (네이버: "애플리케이션간에는 이용자 식별자가 공유되지 않습니다").
 * 그래서 다른 앱이 받은 토큰을 가져와도 그 사람의 Reborn 계정이 아니라 별개의 새 계정이 될 뿐이다.
 * ponytail: 네이버 access token 은 어느 앱에 발급됐는지 확인할 API 가 없다 (/v1/nid/verify 도 client 를 안 알려줌).
 * 꼭 묶어야 하면 네이버 OIDC(id_token 의 aud = 우리 client id)를 서버가 직접 받는 방식으로.
 */
internal data class NaverUser(val id: String, val name: String?, val avatarUrl: String?)

/**
 * 네이버 로그인 → Supabase 세션. Supabase 가 네이버를 직접 지원하지 않아 서버가 잇는다.
 * 앱이 네이버 SDK 로 받은 access token 으로 네이버에 누군지 묻고 → 그 사람 계정(없으면 만듦)의 세션을 돌려준다.
 * 계정 이메일은 네이버 id 로 만든 가짜 주소(.invalid — 메일이 갈 수 없음). 네이버 이메일로 기존 계정에 붙이지 않는다
 * (네이버 이메일은 소유 확인이 안 된 연락처일 수 있어, 남의 이메일 계정에 들어가는 길이 된다).
 */
/** 로그인 없이 부를 수 있어 동시에 처리하는 수를 묶는다 — 네이버가 느릴 때 몰려와도 다른 API 스레드를 다 잡아먹지 않게 */
private val naverSlots = Semaphore(8)

internal fun Route.naverRoutes(verify: (String) -> NaverUser? = ::fetchNaverUser) {
    post("/auth/naver") {
        val token = call.receive<NaverLoginRequest>().accessToken.trim()
        if (token.isEmpty() || token.length > 2048) {
            return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("missing_token"))
        }
        if (!SupabaseAdmin.configured) {
            return@post call.respond(HttpStatusCode.ServiceUnavailable, ErrorResponse("auth_not_configured"))
        }
        if (!naverSlots.tryAcquire()) return@post call.respond(HttpStatusCode.ServiceUnavailable, ErrorResponse("busy"))
        val session = try {
            // 네이버·Supabase 호출은 막히는(blocking) 호출 → IO 스레드에서
            withContext(Dispatchers.IO) { naverSession(token, verify) }
        } catch (e: Exception) {
            // 네이버·Supabase 가 느리거나 죽음 — 500 대신 "잠시 후 다시"
            return@post call.respond(HttpStatusCode.ServiceUnavailable, ErrorResponse("naver_unavailable"))
        } finally {
            naverSlots.release()
        }
        when (session) {
            NaverSession.InvalidToken -> call.respond(HttpStatusCode.Unauthorized, ErrorResponse("invalid_naver_token"))
            NaverSession.Conflict -> call.respond(HttpStatusCode.Conflict, ErrorResponse("account_conflict"))
            is NaverSession.Issued -> call.respondText(session.json, ContentType.Application.Json)
        }
    }
}

private sealed interface NaverSession {
    object InvalidToken : NaverSession
    object Conflict : NaverSession
    data class Issued(val json: String) : NaverSession
}

private fun naverSession(token: String, verify: (String) -> NaverUser?): NaverSession {
    val naver = verify(token) ?: return NaverSession.InvalidToken
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
    if (!created && !isNaverAccount(email)) return NaverSession.Conflict
    return NaverSession.Issued(SupabaseAdmin.sessionFor(email))
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
