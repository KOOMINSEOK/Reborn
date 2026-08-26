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

const val SUPABASE_AUTH = "supabase"

/**
 * Supabase 가 발급한 액세스 토큰(ES256)을 JWKS 공개키로 검증한다.
 * `SUPABASE_URL` 이 없으면 인증을 비활성화하되 서버는 정상 기동한다 — 보호 라우트는 401.
 */
fun Application.configureSecurity() {
    val supabaseUrl = Env.get("SUPABASE_URL")?.trimEnd('/')
    val logger = log

    install(Authentication) {
        jwt(SUPABASE_AUTH) {
            realm = "reborn"

            if (supabaseUrl.isNullOrBlank()) {
                logger.warn("SUPABASE_URL 미설정 — 인증 비활성화. 보호 엔드포인트는 401 을 반환합니다.")
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
                    credential.payload.subject?.let { sub ->
                        UserPrincipal(id = sub, email = credential.payload.getClaim("email").asString())
                    }
                }
            }

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("unauthorized"))
            }
        }
    }
}

private const val HISTORICAL_KEYS = 10L
private const val CACHE_HOURS = 24L
private const val JWKS_FETCHES_PER_MINUTE = 10L
private const val LEEWAY_SECONDS = 5L
