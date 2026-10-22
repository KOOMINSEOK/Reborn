package com.gentlelady.reborn.server.signup

import com.gentlelady.reborn.server.Env
import com.gentlelady.reborn.server.plugins.ErrorResponse
import com.gentlelady.reborn.server.plugins.SUPABASE_SIGNUP_AUTH
import com.gentlelady.reborn.server.plugins.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.SecureRandom
import java.time.Duration
import java.util.UUID

private val CARRIERS = setOf("SKT", "KT", "LGU+", "SKT 알뜰폰", "KT 알뜰폰", "LGU+ 알뜰폰")
private val PURPOSES = setOf("remember", "record", "browse")
private val random = SecureRandom()

/**
 * 회원가입 단계 API. 계정(세션)은 소셜 로그인·이메일 인증번호로 이미 생긴 뒤라 모두 로그인 필요.
 * 본인인증: 실명 확인 업체 계약 전까진 IDENTITY_DEV=true 일 때만 개발용 대역 (문자 대신 응답에 번호).
 */
fun Route.signupRoutes(repo: SignupRepository) {
    val identityDev = Env.get("IDENTITY_DEV")?.lowercase() == "true"
    // 개발용 본인인증은 응답에 인증번호를 담는다 → 실서비스에서 켜지면 누구나 남의 이름으로 인증된다. 켜져 있으면 기동을 막는다
    check(!(identityDev && Env.get("APP_ENV")?.lowercase() == "production")) {
        "IDENTITY_DEV=true 는 APP_ENV=production 에서 쓸 수 없어요 — 본인확인 업체를 연결하세요"
    }

    authenticate(SUPABASE_SIGNUP_AUTH) {
        get("/me/signup") {
            repo.state(call.userId())?.let { call.respond(it) }
                ?: call.respond(HttpStatusCode.NotFound, ErrorResponse("profile_not_found"))
        }

        post("/me/signup/terms") {
            val req = call.receive<TermsRequest>()
            if (!req.requiredAgreed) return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("required_terms"))
            repo.agreeTerms(call.userId(), req.marketing)
            call.respond(HttpStatusCode.NoContent)
        }

        post("/me/identity/request") {
            val userId = call.userId()
            if (!identityDev) {
                return@post call.respond(HttpStatusCode.ServiceUnavailable, ErrorResponse("identity_provider_not_configured"))
            }
            val req = call.receive<IdentityStartRequest>()
            identityInputError(req)?.let { return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse(it)) }
            val phone = req.phone.filter(Char::isDigit)
            // 개발용 CI: 같은 사람이면 같은 값. 실서비스는 업체가 확인 뒤 돌려준다.
            // 이미 가입한 계정이 있는지는 여기서 알려주지 않는다 — 번호로 본인임을 확인한 뒤에만 (남의 정보로 가입 여부를 떠보지 못하게)
            val ci = hash("dev|${req.name.trim()}|${req.birth}|${req.genderDigit}|$phone")
            val code = (0 until 6).joinToString("") { random.nextInt(10).toString() }
            val (id, expiresAt) = repo.createRequest(userId, ci, req.name.trim(), code)
            call.respond(IdentityStartResponse(id.toString(), expiresAt.toString(), devCode = code))
        }

        post("/me/identity/verify") {
            val userId = call.userId()
            val req = call.receive<IdentityVerifyRequest>()
            val requestId = runCatching { UUID.fromString(req.requestId) }.getOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("bad_request_id"))
            when (val result = repo.verify(userId, requestId, req.code.trim())) {
                is SignupRepository.VerifyResult.Expired ->
                    call.respond(HttpStatusCode.Gone, ErrorResponse("code_expired"))
                is SignupRepository.VerifyResult.Wrong ->
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(if (result.attemptsLeft > 0) "wrong_code" else "too_many_attempts"))
                is SignupRepository.VerifyResult.Verified -> {
                    // 본인임이 확인됐다 → 이제 같은 사람이 이미 가입을 마친 계정이 있는지 알려준다
                    repo.existingAccount(result.ci, userId)?.let {
                        return@post call.respond(HttpStatusCode.Conflict, AlreadyRegisteredResponse(account = it))
                    }
                    if (!repo.saveIdentity(userId, result.ci, result.name)) {
                        return@post call.respond(HttpStatusCode.Conflict, ErrorResponse("identity_in_use"))
                    }
                    call.respond(IdentityVerifyResponse(result.name))
                }
            }
        }

        get("/handles/check") {
            val handle = call.request.queryParameters["handle"].orEmpty().trim()
            call.respond(
                when {
                    !HANDLE_RULE.matches(handle) -> HandleCheckResponse(available = false, reason = "invalid")
                    repo.handleTaken(handle, call.userId()) -> HandleCheckResponse(available = false, reason = "taken")
                    else -> HandleCheckResponse(available = true)
                },
            )
        }

        post("/me/signup/complete") {
            val userId = call.userId()
            val req = call.receive<CompleteSignupRequest>()
            val handle = req.handle.trim()
            if (!HANDLE_RULE.matches(handle)) return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("invalid_handle"))
            if (req.purpose !in PURPOSES) return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("invalid_purpose"))
            if (repo.handleTaken(handle, userId)) return@post call.respond(HttpStatusCode.Conflict, ErrorResponse("handle_taken"))
            if (!repo.complete(userId, handle, req.purpose)) {
                // 약관·본인인증 전이거나, 그 사이 아이디가 잡힘
                val state = repo.state(userId)
                val error = if (state?.termsAgreed == true && state.identityVerified) "handle_taken" else "signup_incomplete"
                return@post call.respond(HttpStatusCode.Conflict, ErrorResponse(error))
            }
            call.respond(HttpStatusCode.NoContent)
        }

        /**
         * 가입을 그만둠 — 아직 가입을 마치지 않은(아이디를 안 만든) 계정만 지운다.
         * "이미 가입된 계정이 있어요"에서 기존 계정으로 가기 전에, 방금 생긴 빈 계정을 정리할 때.
         */
        delete("/me/signup") {
            val userId = call.userId()
            when (repo.markDeleting(userId)) {
                // 이미 지워짐 (다시 누름) — 성공으로
                SignupRepository.MarkDeleting.GONE -> return@delete call.respond(HttpStatusCode.NoContent)
                SignupRepository.MarkDeleting.ONBOARDED ->
                    return@delete call.respond(HttpStatusCode.Conflict, ErrorResponse("already_onboarded"))
                SignupRepository.MarkDeleting.MARKED -> Unit
            }
            // 표시를 찍은 뒤엔 가입 마무리가 안 된다 → 지우는 사이 가입이 끝나 버리는 일이 없다
            if (!SupabaseAdmin.deleteUser(userId)) {
                repo.unmarkDeleting(userId) // 못 지웠으면 다시 가입을 이어갈 수 있게
                return@delete call.respond(HttpStatusCode.ServiceUnavailable, ErrorResponse("delete_failed"))
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

/** 입력 형식만 본다 (진짜 확인은 업체). 문제 있으면 에러 코드. */
internal fun identityInputError(req: IdentityStartRequest): String? {
    val phone = req.phone.filter(Char::isDigit)
    return when {
        req.name.trim().length !in 2..30 -> "invalid_name"
        !Regex("^\\d{6}$").matches(req.birth) -> "invalid_birth"
        req.genderDigit !in setOf("1", "2", "3", "4") -> "invalid_gender"
        req.carrier !in CARRIERS -> "invalid_carrier"
        !Regex("^01\\d{8,9}$").matches(phone) -> "invalid_phone"
        else -> null
    }
}

/** Supabase 관리자 API (서버 비밀키). 가입을 그만둔 빈 계정 지우기에만 쓴다. */
private object SupabaseAdmin {
    private val url = Env.get("SUPABASE_URL")?.trimEnd('/')
    private val key = Env.get("SUPABASE_SECRET_KEY")?.takeIf { it.isNotBlank() }
    private val http by lazy { HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build() }

    fun deleteUser(id: UUID): Boolean {
        if (url == null || key == null) return false
        val req = HttpRequest.newBuilder(URI.create("$url/auth/v1/admin/users/$id"))
            .header("apikey", key)
            .header("Authorization", "Bearer $key")
            .timeout(Duration.ofSeconds(10))
            .DELETE()
            .build()
        return runCatching { http.send(req, HttpResponse.BodyHandlers.discarding()).statusCode() in 200..299 }.getOrDefault(false)
    }
}
