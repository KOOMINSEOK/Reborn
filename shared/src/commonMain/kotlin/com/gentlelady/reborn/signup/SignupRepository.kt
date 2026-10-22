package com.gentlelady.reborn.signup

import com.gentlelady.reborn.core.auth.AuthRepository
import com.gentlelady.reborn.core.network.ImageUploader
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.AlreadyRegisteredDto
import com.gentlelady.reborn.core.network.dto.CompleteSignupDto
import com.gentlelady.reborn.core.network.dto.IdentityStartDto
import com.gentlelady.reborn.core.network.dto.IdentityVerifyDto
import com.gentlelady.reborn.core.network.dto.SignupStateDto
import com.gentlelady.reborn.core.network.dto.TermsDto
import com.gentlelady.reborn.core.network.dto.UpdateProfileDto
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

/** 같은 사람으로 이미 가입을 마친 계정 */
data class ExistingAccount(val provider: String, val handle: String, val joinedAt: String)

sealed interface IdentityStart {
    data class Sent(val requestId: String, val expiresAt: String, val devCode: String?) : IdentityStart
    data class AlreadyRegistered(val account: ExistingAccount) : IdentityStart
    /** 입력 형식 오류 (invalid_name 등) */
    data class Invalid(val code: String) : IdentityStart
    /** 본인확인 업체 미연결 (서버 IDENTITY_DEV 꺼짐) */
    object NotAvailable : IdentityStart
}

sealed interface IdentityVerify {
    data class Verified(val name: String) : IdentityVerify
    object Wrong : IdentityVerify
    object TooManyAttempts : IdentityVerify
    object Expired : IdentityVerify
    data class AlreadyRegistered(val account: ExistingAccount) : IdentityVerify
}

enum class HandleStatus { AVAILABLE, INVALID, TAKEN }

/** 가입 단계 호출 묶음. 실패는 예외(네트워크 등) 또는 위 결과 타입으로. */
interface SignupRepository {
    suspend fun state(): SignupStateDto
    suspend fun requestEmailCode(email: String)
    /** 맞으면 로그인된다 (새 이메일이면 계정이 생긴다) */
    suspend fun verifyEmailCode(email: String, code: String)
    suspend fun setPassword(password: String)
    suspend fun agreeTerms(marketing: Boolean)
    suspend fun requestIdentity(name: String, birth: String, genderDigit: String, carrier: String, phone: String): IdentityStart
    suspend fun verifyIdentity(requestId: String, code: String): IdentityVerify
    suspend fun checkHandle(handle: String): HandleStatus
    /** 가입 마무리. 아이디가 그 사이 잡혔으면 false */
    suspend fun complete(handle: String, purpose: String, avatar: ByteArray?): Boolean
    /** 카카오 등 브라우저 로그인 주소 */
    suspend fun oauthUrl(provider: String): String
    /** 브라우저 로그인에서 돌아온 코드로 로그인 (새 사람이면 계정이 생긴다) */
    suspend fun completeOAuth(code: String)
    /** 네이버 토큰으로 로그인 (새 사람이면 계정이 생긴다) */
    suspend fun naverLogin(accessToken: String)
    /** 가입을 그만두고 로그아웃 (서버가 가입 중인 계정을 지운다). 서버가 못 지우면 예외 — 로그아웃도 안 한다 */
    suspend fun abandon()
}

class SignupRepositoryImpl(
    private val api: RebornApi,
    private val auth: AuthRepository,
    private val uploader: ImageUploader,
) : SignupRepository {
    override suspend fun state(): SignupStateDto = api.signupState()

    override suspend fun requestEmailCode(email: String) = auth.requestEmailCode(email)
    override suspend fun verifyEmailCode(email: String, code: String) = auth.verifyEmailCode(email, code)
    override suspend fun setPassword(password: String) = auth.setPassword(password)

    override suspend fun agreeTerms(marketing: Boolean) = api.agreeTerms(TermsDto(requiredAgreed = true, marketing = marketing))

    override suspend fun requestIdentity(name: String, birth: String, genderDigit: String, carrier: String, phone: String): IdentityStart =
        try {
            api.requestIdentity(IdentityStartDto(name, birth, genderDigit, carrier, phone))
                .let { IdentityStart.Sent(it.requestId, it.expiresAt, it.devCode) }
        } catch (e: ClientRequestException) {
            val body = e.response.bodyAsText()
            when (e.response.status.value) {
                409 -> IdentityStart.AlreadyRegistered(parseExisting(body))
                400 -> IdentityStart.Invalid(errorCode(body))
                else -> throw e
            }
        } catch (e: io.ktor.client.plugins.ServerResponseException) {
            if (e.response.status.value == 503) IdentityStart.NotAvailable else throw e
        }

    override suspend fun verifyIdentity(requestId: String, code: String): IdentityVerify =
        try {
            IdentityVerify.Verified(api.verifyIdentity(IdentityVerifyDto(requestId, code)).name)
        } catch (e: ClientRequestException) {
            val body = e.response.bodyAsText()
            when {
                e.response.status.value == 410 -> IdentityVerify.Expired
                "too_many_attempts" in body -> IdentityVerify.TooManyAttempts
                "wrong_code" in body -> IdentityVerify.Wrong
                "already_registered" in body -> IdentityVerify.AlreadyRegistered(parseExisting(body))
                else -> throw e
            }
        }

    override suspend fun checkHandle(handle: String): HandleStatus =
        api.checkHandle(handle).let {
            when {
                it.available -> HandleStatus.AVAILABLE
                it.reason == "taken" -> HandleStatus.TAKEN
                else -> HandleStatus.INVALID
            }
        }

    /** 가입 마무리. 아이디가 그 사이 잡혔으면 false. 사진은 고른 경우에만 올린다 (실패해도 가입은 된다). */
    override suspend fun complete(handle: String, purpose: String, avatar: ByteArray?): Boolean {
        try {
            api.completeSignup(CompleteSignupDto(handle, purpose))
        } catch (e: ClientRequestException) {
            if (e.response.status.value == 409 && "handle_taken" in e.response.bodyAsText()) return false
            throw e
        }
        if (avatar != null) {
            runCatching {
                val url = uploader.upload(listOf(avatar)).single()
                api.updateMyProfile(UpdateProfileDto(avatarUrl = url))
            }
        }
        return true
    }

    /** 가입을 그만두고 이 기기에서 로그아웃 (서버가 가입 중인 계정을 지운다). 서버 실패해도 로그아웃은 한다. */
    override suspend fun oauthUrl(provider: String) = auth.oauthUrl(provider)
    override suspend fun completeOAuth(code: String) = auth.completeOAuth(code)
    override suspend fun naverLogin(accessToken: String) = auth.signInWith(api.naverLogin(accessToken))

    /** 서버가 계정을 지운 뒤에만 로그아웃 — 실패하면 예외 (화면이 다시 시도를 띄운다. 계정·동의 기록이 몰래 남지 않게) */
    override suspend fun abandon() {
        api.abandonSignup()
        auth.signOut()
    }

    private fun parseExisting(body: String): ExistingAccount =
        json.decodeFromString(AlreadyRegisteredDto.serializer(), body).account.let { ExistingAccount(it.provider, it.handle, it.joinedAt) }

    private fun errorCode(body: String): String =
        Regex("\"error\"\\s*:\\s*\"([a-z_]+)\"").find(body)?.groupValues?.get(1) ?: "invalid"

    private companion object {
        val json = Json { ignoreUnknownKeys = true }
    }
}
