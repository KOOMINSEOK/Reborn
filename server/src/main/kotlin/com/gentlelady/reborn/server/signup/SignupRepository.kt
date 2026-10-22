package com.gentlelady.reborn.server.signup

import com.gentlelady.reborn.server.plugins.Db
import java.security.MessageDigest
import java.time.OffsetDateTime
import java.util.UUID

/** 아이디 규칙: 영문 소문자·숫자·밑줄·마침표, 4~20자 (가입 화면 안내와 같다) */
val HANDLE_RULE = Regex("^[a-z0-9._]{4,20}$")

const val IDENTITY_TTL_SECONDS = 180L
const val IDENTITY_MAX_ATTEMPTS = 5

class SignupRepository {

    fun state(userId: UUID): SignupStateResponse? =
        Db.queryFirst(
            """
            select p.terms_agreed_at is not null as terms, p.identity_verified_at is not null as identity,
                   p.identity_name, p.onboarded_at is not null as onboarded,
                   coalesce(u.raw_app_meta_data ->> 'provider', 'email') as provider,
                   -- 인증번호로 만든 계정도 내부 비밀번호 값이 있어 그걸로는 모른다 → 앱이 비밀번호를 정할 때 같은 요청에 남기는 표시
                   coalesce(u.raw_user_meta_data ->> 'password_set', '') = 'true' as has_password
            from profiles p join auth.users u on u.id = p.id
            where p.id = ?
            """.trimIndent(),
            userId,
        ) {
            val provider = it.getString("provider")
            SignupStateResponse(
                termsAgreed = it.getBoolean("terms"),
                identityVerified = it.getBoolean("identity"),
                identityName = it.getString("identity_name"),
                // 소셜 가입은 비밀번호가 없어도 된다
                hasPassword = provider != "email" || it.getBoolean("has_password"),
                provider = provider,
                onboarded = it.getBoolean("onboarded"),
            )
        }

    fun agreeTerms(userId: UUID, marketing: Boolean): Int =
        Db.update(
            "update profiles set terms_agreed_at = coalesce(terms_agreed_at, now()), marketing_opt_in = ? where id = ?",
            marketing, userId,
        )

    /** 같은 사람(CI)으로 가입을 마친 다른 계정. 가입 중(아직 아이디 안 만든) 계정은 세지 않는다. */
    fun existingAccount(ci: String, exceptUserId: UUID): ExistingAccount? =
        Db.queryFirst(
            """
            select coalesce(u.raw_app_meta_data ->> 'provider', 'email') as provider, p.handle, p.created_at
            from profiles p join auth.users u on u.id = p.id
            where p.identity_ci = ? and p.id <> ? and p.onboarded_at is not null
            """.trimIndent(),
            ci, exceptUserId,
        ) {
            ExistingAccount(
                provider = it.getString("provider"),
                handle = it.getString("handle"),
                joinedAt = it.getObject("created_at", OffsetDateTime::class.java).toString(),
            )
        }

    fun createRequest(userId: UUID, ci: String, name: String, code: String): Pair<UUID, OffsetDateTime> =
        Db.queryFirst(
            """
            insert into identity_requests (user_id, ci, name, code_hash, expires_at)
            values (?, ?, ?, ?, now() + make_interval(secs => ?))
            returning id, expires_at
            """.trimIndent(),
            userId, ci, name, hash(code), IDENTITY_TTL_SECONDS.toDouble(),
        ) { UUID.fromString(it.getString("id")) to it.getObject("expires_at", OffsetDateTime::class.java) }!!

    sealed interface VerifyResult {
        data class Verified(val name: String, val ci: String) : VerifyResult
        /** 없거나 남의 요청 · 만료 · 이미 씀 · 5번 넘게 틀림 */
        object Expired : VerifyResult
        data class Wrong(val attemptsLeft: Int) : VerifyResult
    }

    /**
     * 인증번호 확인. 틀린 횟수는 같은 문장에서 올려 동시에 여러 번 보내도 5번을 넘지 못한다.
     * 맞으면 그 요청을 다 쓴 것으로 표시 (같은 번호로 두 번 인증되지 않게).
     */
    fun verify(userId: UUID, requestId: UUID, code: String): VerifyResult {
        val row = Db.queryFirst(
            """
            update identity_requests set attempts = attempts + 1
            where id = ? and user_id = ? and verified_at is null and expires_at > now() and attempts < ?
            returning code_hash, attempts, name, ci
            """.trimIndent(),
            requestId, userId, IDENTITY_MAX_ATTEMPTS,
        ) { listOf(it.getString("code_hash"), it.getInt("attempts").toString(), it.getString("name"), it.getString("ci")) }
            ?: return VerifyResult.Expired
        val (codeHash, attempts, name, ci) = row
        if (codeHash != hash(code)) return VerifyResult.Wrong(IDENTITY_MAX_ATTEMPTS - attempts.toInt())
        val used = Db.update(
            "update identity_requests set verified_at = now() where id = ? and verified_at is null",
            requestId,
        )
        return if (used == 1) VerifyResult.Verified(name, ci) else VerifyResult.Expired
    }

    /**
     * 본인인증 결과를 프로필에 남긴다. 표시 이름도 실명으로 (가입 화면의 "인증됨" 이름).
     * 같은 CI 가 이미 다른 계정에 있으면(동시에 두 계정으로 인증) unique 제약이 막는다 → false.
     */
    fun saveIdentity(userId: UUID, ci: String, name: String): Boolean =
        runCatching {
            // 같은 사람이 예전에 인증만 하고 가입을 안 마친 계정이 그 CI 를 잡고 있으면 풀어 준다 —
            // 방금 번호로 본인임을 확인한 쪽이 우선 (안 그러면 그 사람은 영영 가입을 못 한다)
            Db.update(
                """
                update profiles set identity_ci = null, identity_verified_at = null, identity_name = null
                where identity_ci = ? and id <> ? and onboarded_at is null
                """.trimIndent(),
                ci, userId,
            )
            Db.update(
                """
                update profiles set identity_verified_at = now(), identity_ci = ?, identity_name = ?, display_name = ?
                where id = ?
                """.trimIndent(),
                ci, name, name, userId,
            ) == 1
        }.getOrElse { false }

    fun handleTaken(handle: String, exceptUserId: UUID): Boolean =
        Db.queryFirst("select 1 from profiles where handle = ? and id <> ?", handle, exceptUserId) { true } == true

    /** 가입 마무리. 약관·본인인증이 끝났을 때만. 아이디가 그 사이 잡혔으면 unique 제약이 막는다 → false. */
    fun complete(userId: UUID, handle: String, purpose: String): Boolean =
        runCatching {
            Db.update(
                """
                update profiles set handle = ?, signup_purpose = ?, onboarded_at = coalesce(onboarded_at, now())
                where id = ? and terms_agreed_at is not null and identity_verified_at is not null and deleting_at is null
                """.trimIndent(),
                handle, purpose, userId,
            ) == 1
        }.getOrElse { false }

    enum class MarkDeleting { MARKED, ONBOARDED, GONE }

    /** 가입을 마치지 않은 계정에만 "지우는 중" 표시 (한 문장 — 가입 마무리와 동시에 와도 둘 중 하나만). */
    fun markDeleting(userId: UUID): MarkDeleting {
        val marked = Db.update(
            "update profiles set deleting_at = now() where id = ? and onboarded_at is null",
            userId,
        )
        if (marked == 1) return MarkDeleting.MARKED
        val exists = Db.queryFirst("select 1 from profiles where id = ?", userId) { true } == true
        return if (exists) MarkDeleting.ONBOARDED else MarkDeleting.GONE
    }

    fun unmarkDeleting(userId: UUID): Int = Db.update("update profiles set deleting_at = null where id = ?", userId)
}

/** 개발용 CI · 인증번호 해시. ponytail: 소금 없는 sha256 — 실서비스 CI 는 업체가 준다. */
internal fun hash(value: String): String =
    MessageDigest.getInstance("SHA-256").digest(value.toByteArray()).joinToString("") { "%02x".format(it) }
