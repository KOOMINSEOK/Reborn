package com.gentlelady.reborn.signup

/** 앱 첫 화면을 무엇으로 할지 */
enum class SignupGate {
    /** 저장된 세션의 가입 상태를 묻는 중 */
    CHECKING,
    /** 가입 화면 (로그인 안 함 또는 가입이 덜 끝남) */
    SIGNUP,
    /** 가입 끝 → 앱으로 */
    DONE,
}

enum class SignupStep { START, TERMS, EMAIL, EMAIL_CODE, PASSWORD, IDENTITY, IDENTITY_CODE, HANDLE, PURPOSE, NOTIFICATION }

/** 이메일로 시작 / 소셜로 시작 — 단계가 조금 다르다 (이메일은 이메일·인증번호·비밀번호가 더 있다) */
enum class SignupMethod { EMAIL, SOCIAL }

enum class SignupPurpose(val code: String) { REMEMBER("remember"), RECORD("record"), BROWSE("browse") }

data class TermsForm(
    val age: Boolean = false,
    val service: Boolean = false,
    val privacy: Boolean = false,
    val marketing: Boolean = false,
) {
    val requiredAgreed: Boolean get() = age && service && privacy
    val allAgreed: Boolean get() = requiredAgreed && marketing
}

enum class Carrier(val label: String) {
    SKT("SKT"), KT("KT"), LGU("LGU+"), SKT_MVNO("SKT 알뜰폰"), KT_MVNO("KT 알뜰폰"), LGU_MVNO("LGU+ 알뜰폰"),
}

data class IdentityForm(
    val name: String = "",
    /** 생년월일 6자리 */
    val birth: String = "",
    /** 주민번호 뒷자리 첫 숫자 */
    val genderDigit: String = "",
    val carrier: Carrier = Carrier.SKT,
    /** 숫자만 */
    val phone: String = "",
    val agreed: Boolean = false,
) {
    val isComplete: Boolean
        get() = name.trim().length >= 2 && birth.length == 6 && genderDigit.length == 1 &&
            phone.length in 10..11 && phone.startsWith("01") && agreed
}

enum class HandleCheck { IDLE, CHECKING, AVAILABLE, INVALID, TAKEN }

data class SignupState(
    val gate: SignupGate = SignupGate.CHECKING,
    val step: SignupStep = SignupStep.START,
    val method: SignupMethod = SignupMethod.EMAIL,
    /** email · kakao · naver · google */
    val provider: String = "email",
    /** 시작 화면의 "로그인" → 기존 이메일 로그인 화면 */
    val showLogin: Boolean = false,
    val terms: TermsForm = TermsForm(),
    val email: String = "",
    val emailCode: String = "",
    /** 인증번호 유효 시각 (epoch ms) — 화면이 남은 시간을 센다 */
    val emailCodeDeadline: Long = 0,
    val password: String = "",
    val passwordConfirm: String = "",
    val identity: IdentityForm = IdentityForm(),
    val identityRequestId: String? = null,
    val identityCode: String = "",
    val identityDeadline: Long = 0,
    /** 개발용 본인인증에서만 — 문자 대신 화면에 보여준다 */
    val devCode: String? = null,
    /** 본인인증으로 확인된 이름 (아이디 화면의 "인증됨") */
    val verifiedName: String = "",
    /** "이미 가입된 계정이 있어요" 시트 */
    val existingAccount: ExistingAccount? = null,
    val handle: String = "",
    val handleCheck: HandleCheck = HandleCheck.IDLE,
    val avatar: ByteArray? = null,
    val purpose: SignupPurpose? = null,
    val isBusy: Boolean = false,
    /** 한 번 보여주고 지우는 안내 */
    val message: String? = null,
    /** 입력칸 아래 빨간 글씨 (인증번호 틀림 등) */
    val fieldError: String? = null,
    /** "가입을 그만둘까요?" */
    val showAbandonConfirm: Boolean = false,
    /** 네이버: 이 기기 계정으로 계속 / 다른 계정으로 로그인 고르는 시트 */
    val showNaverChooser: Boolean = false,
) {
    val passwordRules: PasswordRules get() = PasswordRules.of(password)
    val passwordsMatch: Boolean get() = password.isNotEmpty() && password == passwordConfirm
}

/** 비밀번호 규칙 — 8자 이상, 영문·숫자·특수문자 중 2가지 이상 */
data class PasswordRules(val longEnough: Boolean, val mixed: Boolean) {
    val ok: Boolean get() = longEnough && mixed

    companion object {
        fun of(password: String) = PasswordRules(
            longEnough = password.length >= 8,
            mixed = listOf(
                password.any { it.isLetter() },
                password.any { it.isDigit() },
                password.any { !it.isLetterOrDigit() && !it.isWhitespace() },
            ).count { it } >= 2,
        )
    }
}

/** 아이디 규칙 (서버와 같다): 영문 소문자·숫자·밑줄·마침표, 4~20자 */
val HANDLE_RULE = Regex("^[a-z0-9._]{4,20}$")

/** 이메일 모양만 본다 (진짜 확인은 인증번호) */
val EMAIL_RULE = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

sealed interface SignupIntent {
    data class Start(val method: SignupMethod, val provider: String) : SignupIntent
    object OpenLogin : SignupIntent
    object CloseLogin : SignupIntent
    object Back : SignupIntent
    data class UpdateTerms(val terms: TermsForm) : SignupIntent
    object SubmitTerms : SignupIntent
    data class UpdateEmail(val email: String) : SignupIntent
    object SendEmailCode : SignupIntent
    data class UpdateEmailCode(val code: String) : SignupIntent
    object VerifyEmailCode : SignupIntent
    data class UpdatePassword(val password: String, val confirm: String) : SignupIntent
    object SubmitPassword : SignupIntent
    data class UpdateIdentity(val form: IdentityForm) : SignupIntent
    object SendIdentityCode : SignupIntent
    data class UpdateIdentityCode(val code: String) : SignupIntent
    object VerifyIdentityCode : SignupIntent
    /** "이미 가입된 계정" 시트의 "○○로 로그인" — 가입 중인 빈 계정을 지우고 기존 계정 로그인으로 */
    object UseExistingAccount : SignupIntent
    object DismissExistingAccount : SignupIntent
    data class NaverLogin(val otherAccount: Boolean) : SignupIntent
    object DismissNaverChooser : SignupIntent
    data class UpdateHandle(val handle: String) : SignupIntent
    data class UpdateAvatar(val jpeg: ByteArray?) : SignupIntent
    object SubmitHandle : SignupIntent
    data class SelectPurpose(val purpose: SignupPurpose) : SignupIntent
    object SubmitPurpose : SignupIntent
    /** 알림 단계 끝 (알림 받기·나중에 둘 다) */
    object Finish : SignupIntent
    object ConfirmAbandon : SignupIntent
    object DismissAbandon : SignupIntent
    object MessageShown : SignupIntent
}
