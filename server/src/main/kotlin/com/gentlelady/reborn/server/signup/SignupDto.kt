package com.gentlelady.reborn.server.signup

import kotlinx.serialization.Serializable

/** 가입이 어디까지 됐나 — 앱이 이 값으로 남은 단계부터 이어서 보여준다. */
@Serializable
data class SignupStateResponse(
    val termsAgreed: Boolean,
    val identityVerified: Boolean,
    /** 본인인증으로 확인된 이름 (가입 화면의 "인증됨" 이름) */
    val identityName: String? = null,
    /** 이메일 가입이면 비밀번호를 정했나 (소셜 가입은 항상 true) */
    val hasPassword: Boolean,
    /** email · kakao · naver · google … */
    val provider: String,
    val onboarded: Boolean,
)

@Serializable
data class TermsRequest(
    /** 필수 약관(만 14세·이용약관·개인정보)을 모두 동의했나 — 앱이 막지만 서버도 확인 */
    val requiredAgreed: Boolean,
    val marketing: Boolean = false,
)

@Serializable
data class IdentityStartRequest(
    val name: String,
    /** 생년월일 6자리 (yyMMdd) */
    val birth: String,
    /** 주민번호 뒷자리 첫 숫자 (1~4). 이것만 받는다 */
    val genderDigit: String,
    /** SKT · KT · LGU+ · 알뜰폰 */
    val carrier: String,
    val phone: String,
)

@Serializable
data class IdentityStartResponse(
    val requestId: String,
    val expiresAt: String,
    /** 개발용 대역에서만 — 문자 대신 화면에 보여줄 인증번호 */
    val devCode: String? = null,
)

@Serializable
data class IdentityVerifyRequest(val requestId: String, val code: String)

@Serializable
data class IdentityVerifyResponse(val name: String)

/** 같은 사람이 이미 가입한 계정 ("이미 가입된 계정이 있어요" 시트) */
@Serializable
data class ExistingAccount(val provider: String, val handle: String, val joinedAt: String)

@Serializable
data class AlreadyRegisteredResponse(val error: String = "already_registered", val account: ExistingAccount)

@Serializable
data class HandleCheckResponse(
    val available: Boolean,
    /** invalid(규칙에 안 맞음) · taken(이미 씀) */
    val reason: String? = null,
)

@Serializable
data class CompleteSignupRequest(
    val handle: String,
    /** remember · record · browse */
    val purpose: String,
)
