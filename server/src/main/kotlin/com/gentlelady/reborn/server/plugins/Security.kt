package com.gentlelady.reborn.server.plugins

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.gentlelady.reborn.server.Env
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import java.net.URI
import java.util.concurrent.TimeUnit

/** 가입을 마친 계정만 (거의 모든 API). 가입 중이면 403 signup_incomplete */
const val SUPABASE_AUTH = "supabase"

/** 로그인만 돼 있으면 되는 것 — 가입 단계 API 전용 (약관·본인인증·아이디·가입 상태) */
const val SUPABASE_SIGNUP_AUTH = "supabase-signup"

private val SignupIncomplete = io.ktor.util.AttributeKey<Boolean>("signupIncomplete")

/**
 * Supabase 가 발급한 액세스 토큰(ES256)을 JWKS 공개키로 검증한다.
 * `SUPABASE_URL` 이 없으면 인증을 비활성화하되 서버는 정상 기동한다 — 보호 라우트는 401.
 */
fun Application.configureSecurity() {
    val supabaseUrl = Env.get("SUPABASE_URL")?.trimEnd('/')
    val logger = log

    install(Authentication) {
        // 같은 토큰 검증을 두 이름으로. 일반 API 는 가입까지 마쳤는지도 본다 (앱 화면만 막으면 API 를 직접 부를 수 있다)
        listOf(SUPABASE_AUTH, SUPABASE_SIGNUP_AUTH).forEach { name ->
            jwt(name) {
                realm = "reborn"

                if (supabaseUrl.isNullOrBlank()) {
                    if (name == SUPABASE_AUTH) logger.warn("SUPABASE_URL 미설정 — 인증 비활성화. 보호 엔드포인트는 401 을 반환합니다.")
                    // ponytail: 실제 ES256 토큰과 알고리즘이 안 맞는 더미 검증기 → 항상 거부.
                    verifier(JWT.require(Algorithm.HMAC256("auth-disabled")).build())
                    validate { null }
                } else {
                    val issuer = "$supabaseUrl/auth/v1"
                    val jwkProvider = JwkProviderBuilder(URI("$issuer/.well-known/jwks.json").toURL())
                        .cached(HISTORICAL_KEYS, CACHE_HOURS, TimeUnit.HOURS)
                        .rateLimited(JWKS_FETCHES_PER_MINUTE, 1, TimeUnit.MINUTES)
                        .build()

                    verifier(jwkProvider, issuer) {
                        withAudience("authenticated")
                        acceptLeeway(LEEWAY_SECONDS)
                    }
                    validate { credential ->
                        val sub = credential.payload.subject ?: return@validate null
                        if (name == SUPABASE_AUTH && !Onboarding.isOnboarded(sub)) {
                            attributes.put(SignupIncomplete, true)
                            return@validate null
                        }
                        UserPrincipal(id = sub, email = credential.payload.getClaim("email").asString())
                    }
                }

                challenge { _, _ ->
                    if (call.attributes.getOrNull(SignupIncomplete) == true) {
                        call.respond(HttpStatusCode.Forbidden, ErrorResponse("signup_incomplete"))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, ErrorResponse("unauthorized"))
                    }
                }
            }
        }
    }
}

/**
 * 가입을 마쳤나. 마친 계정은 다시 안 묻게 기억한다 (가입 완료는 되돌아가지 않는다 — 계정이 지워지면 토큰도 못 쓴다).
 * ponytail: 서버 한 대의 메모리 캐시. 여러 대로 늘리면 각자 캐시 — 그래도 맞다 (처음 한 번 DB 를 볼 뿐).
 */
object Onboarding {
    private val done = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

    fun isOnboarded(userId: String): Boolean {
        if (userId in done) return true
        val ok = runCatching {
            Db.queryFirst("select 1 from profiles where id = ?::uuid and onboarded_at is not null", userId) { true } == true
        }.getOrDefault(false)
        if (ok) done += userId
        return ok
    }
}

private const val HISTORICAL_KEYS = 10L
private const val CACHE_HOURS = 24L
private const val JWKS_FETCHES_PER_MINUTE = 10L
// 기기·서버 시계 오차 허용 (로컬 PC 가 Supabase 보다 6초 느려 갓 발급된 토큰의 iat 가 미래로 보인 적 있음)
private const val LEEWAY_SECONDS = 60L
