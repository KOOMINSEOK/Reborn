package com.gentlelady.reborn.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.core.auth.EmailRateLimitedException
import com.gentlelady.reborn.core.auth.InvalidCodeException
import com.gentlelady.reborn.core.auth.WeakPasswordException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

private const val EMAIL_CODE_MS = 5 * 60_000L
private const val IDENTITY_CODE_MS = 3 * 60_000L
private const val HANDLE_CHECK_DEBOUNCE_MS = 400L

/**
 * 회원가입 전체 (앱 첫 화면 결정 포함).
 * - 로그인 안 함 → 시작 화면. 세션이 있는데 가입이 덜 끝났으면 서버가 아는 다음 단계부터 이어서.
 * - 이메일: 약관 → 이메일 → 인증번호(여기서 계정 생김) → 비밀번호 → 본인인증 → 아이디 → 목적 → 알림
 * - 소셜: (소셜 로그인으로 계정 생김) → 약관 → 본인인증 → 아이디 → 목적 → 알림
 * 계정이 생긴 뒤 처음 단계에서 뒤로 가면 "가입을 그만둘까요?" → 서버가 빈 계정을 지운다.
 */
class SignupViewModel(
    private val repository: SignupRepository,
    private val currentUserId: CurrentUserIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SignupState())
    val state: StateFlow<SignupState> = _state.asStateFlow()

    /** 가입 흐름 안에서 로그인시킬 때(이메일 인증번호) — 세션 변화에 따른 자동 이어하기를 잠시 막는다 */
    private var resumingInFlow = false
    private var handleJob: Job? = null
    private var resumeJob: Job? = null

    init {
        viewModelScope.launch {
            currentUserId.changes.collect { userId ->
                if (userId == null) {
                    // 로그아웃·가입 포기·처음 → 시작 화면 (이메일 입력 중이면 그대로)
                    _state.update { s ->
                        val preAccount = s.step in setOf(SignupStep.TERMS, SignupStep.EMAIL, SignupStep.EMAIL_CODE) &&
                            s.method == SignupMethod.EMAIL && s.gate == SignupGate.SIGNUP
                        if (preAccount) s else SignupState(gate = SignupGate.SIGNUP, showLogin = s.showLogin, message = s.message)
                    }
                } else if (!resumingInFlow) {
                    resume(fromAppStart = _state.value.gate == SignupGate.CHECKING)
                }
            }
        }
    }

    fun onIntent(intent: SignupIntent) {
        when (intent) {
            is SignupIntent.Start -> start(intent.method, intent.provider)
            is SignupIntent.OpenLogin -> _state.update { it.copy(showLogin = true) }
            is SignupIntent.CloseLogin -> _state.update { it.copy(showLogin = false) }
            is SignupIntent.Back -> back()
            is SignupIntent.UpdateTerms -> _state.update { it.copy(terms = intent.terms) }
            is SignupIntent.SubmitTerms -> submitTerms()
            is SignupIntent.UpdateEmail -> _state.update { it.copy(email = intent.email.trim(), fieldError = null) }
            is SignupIntent.SendEmailCode -> sendEmailCode()
            is SignupIntent.UpdateEmailCode -> _state.update { it.copy(emailCode = intent.code.filter(Char::isDigit).take(6), fieldError = null) }
            is SignupIntent.VerifyEmailCode -> verifyEmailCode()
            is SignupIntent.UpdatePassword -> _state.update { it.copy(password = intent.password, passwordConfirm = intent.confirm) }
            is SignupIntent.SubmitPassword -> submitPassword()
            is SignupIntent.UpdateIdentity -> _state.update { it.copy(identity = intent.form, fieldError = null) }
            is SignupIntent.SendIdentityCode -> sendIdentityCode()
            is SignupIntent.UpdateIdentityCode -> _state.update { it.copy(identityCode = intent.code.filter(Char::isDigit).take(6), fieldError = null) }
            is SignupIntent.VerifyIdentityCode -> verifyIdentityCode()
            is SignupIntent.UseExistingAccount -> useExistingAccount()
            is SignupIntent.DismissExistingAccount -> _state.update { it.copy(existingAccount = null) }
            is SignupIntent.UpdateHandle -> updateHandle(intent.handle)
            is SignupIntent.UpdateAvatar -> _state.update { it.copy(avatar = intent.jpeg) }
            is SignupIntent.SubmitHandle -> submitHandle()
            is SignupIntent.SelectPurpose -> _state.update { it.copy(purpose = intent.purpose) }
            is SignupIntent.SubmitPurpose -> submitPurpose()
            is SignupIntent.Finish -> _state.update { it.copy(gate = SignupGate.DONE) }
            is SignupIntent.ConfirmAbandon -> abandon()
            is SignupIntent.DismissAbandon -> _state.update { it.copy(showAbandonConfirm = false) }
            is SignupIntent.MessageShown -> _state.update { it.copy(message = null) }
        }
    }

    // ---------- 흐름 ----------

    private fun start(method: SignupMethod, provider: String) {
        if (method == SignupMethod.SOCIAL) {
            // ponytail: 카카오·네이버·Google 키를 받기 전까지 버튼만 — 소셜 로그인이 붙으면 세션이 생기고 resume() 이 약관부터 잇는다
            val name = when (provider) { "kakao" -> "카카오"; "naver" -> "네이버"; else -> "Google" }
            _state.update { it.copy(message = "${name} 로그인은 준비 중이에요. 지금은 이메일로 시작해 주세요.") }
            return
        }
        _state.update { SignupState(gate = SignupGate.SIGNUP, step = SignupStep.TERMS, method = method, provider = provider) }
    }

    /** 서버가 아는 가입 진행 상태로 다음 단계를 정한다. 앱을 켰을 때 묻다 실패하면(오프라인) 그냥 앱으로. */
    private fun resume(fromAppStart: Boolean) {
        resumeJob?.cancel()
        resumeJob = viewModelScope.launch {
            val result = runCatching { repository.state() }
            if (result.exceptionOrNull() is CancellationException) return@launch
            val server = result.getOrNull()
            if (server == null) {
                _state.update {
                    if (fromAppStart) it.copy(gate = SignupGate.DONE)
                    else it.copy(isBusy = false, message = "연결이 불안정해요. 잠시 후 다시 시도해 주세요.")
                }
                return@launch
            }
            if (server.onboarded) {
                _state.update { it.copy(gate = SignupGate.DONE, isBusy = false) }
                return@launch
            }
            val method = if (server.provider == "email") SignupMethod.EMAIL else SignupMethod.SOCIAL
            val next = when {
                !server.termsAgreed -> SignupStep.TERMS
                !server.hasPassword -> SignupStep.PASSWORD
                !server.identityVerified -> SignupStep.IDENTITY
                else -> SignupStep.HANDLE
            }
            _state.update {
                it.copy(
                    gate = SignupGate.SIGNUP,
                    step = next,
                    // 약관부터 다시면 체크를 비운다 (같은 폰이어도 다른 사람일 수 있다)
                    terms = if (next == SignupStep.TERMS) TermsForm() else it.terms,
                    method = method,
                    provider = server.provider,
                    verifiedName = server.identityName.orEmpty(),
                    isBusy = false,
                    showLogin = false,
                )
            }
        }
    }

    private fun back() {
        val s = _state.value
        if (s.isBusy) return
        if (s.existingAccount != null) {
            _state.update { it.copy(existingAccount = null) }
            return
        }
        val hasAccount = currentUserId() != null
        val back: Back = when (s.step) {
            SignupStep.START -> Back.Stay
            SignupStep.TERMS -> if (hasAccount) Back.Abandon else Back.To(SignupStep.START)
            SignupStep.EMAIL -> Back.To(SignupStep.TERMS)
            SignupStep.EMAIL_CODE -> Back.To(SignupStep.EMAIL)
            // 계정이 생긴 뒤 첫 화면들 — 더 뒤로는 못 간다 (그만두기만)
            SignupStep.PASSWORD, SignupStep.IDENTITY, SignupStep.HANDLE -> Back.Abandon
            SignupStep.IDENTITY_CODE -> Back.To(SignupStep.IDENTITY)
            SignupStep.PURPOSE -> Back.To(SignupStep.HANDLE)
            // 가입은 이미 끝났다 — 알림 안내만 건너뛴다
            SignupStep.NOTIFICATION -> Back.Finish
        }
        when (back) {
            Back.Stay -> Unit
            Back.Abandon -> _state.update { it.copy(showAbandonConfirm = true) }
            Back.Finish -> _state.update { it.copy(gate = SignupGate.DONE) }
            is Back.To -> _state.update { it.copy(step = back.step, fieldError = null) }
        }
    }

    private fun submitTerms() {
        val s = _state.value
        if (!s.terms.requiredAgreed || s.isBusy) return
        if (currentUserId() == null) {
            // 이메일 가입: 아직 계정이 없다 → 동의는 들고 있다가 인증번호로 계정이 생기면 보낸다
            _state.update { it.copy(step = SignupStep.EMAIL) }
            return
        }
        busy {
            repository.agreeTerms(s.terms.marketing)
            resume(fromAppStart = false)
        }
    }

    private fun sendEmailCode() {
        val s = _state.value
        if (!EMAIL_RULE.matches(s.email) || s.isBusy) return
        busy(onError = { e ->
            if (e is EmailRateLimitedException) "인증 메일을 너무 자주 요청했어요. 잠시 후 다시 시도해 주세요."
            else "인증 메일을 보내지 못했어요. 이메일 주소를 확인해 주세요."
        }) {
            repository.requestEmailCode(s.email)
            _state.update {
                it.copy(
                    step = SignupStep.EMAIL_CODE,
                    emailCode = "",
                    emailCodeDeadline = now() + EMAIL_CODE_MS,
                    isBusy = false,
                    message = if (s.step == SignupStep.EMAIL_CODE) "인증 메일을 다시 보냈어요." else null,
                )
            }
        }
    }

    private fun verifyEmailCode() {
        val s = _state.value
        if (s.emailCode.length != 6 || s.isBusy) return
        if (now() > s.emailCodeDeadline) {
            _state.update { it.copy(fieldError = "인증번호가 만료됐어요. 다시 받아 주세요.") }
            return
        }
        resumingInFlow = true
        busy(
            onError = { e -> if (e is InvalidCodeException) null else "인증하지 못했어요. 잠시 후 다시 시도해 주세요." },
            onFieldError = { e -> if (e is InvalidCodeException) "인증번호가 맞지 않아요." else null },
            finally = { resumingInFlow = false },
        ) {
            repository.verifyEmailCode(s.email, s.emailCode) // 여기서 계정(세션)이 생긴다
            if (s.terms.requiredAgreed) repository.agreeTerms(s.terms.marketing)
            resume(fromAppStart = false) // 가입을 이미 마친 이메일이면 그대로 앱으로
        }
    }

    private fun submitPassword() {
        val s = _state.value
        if (!s.passwordRules.ok || !s.passwordsMatch || s.isBusy) return
        busy(onError = { e ->
            if (e is WeakPasswordException) "더 안전한 비밀번호를 써 주세요." else "비밀번호를 저장하지 못했어요. 잠시 후 다시 시도해 주세요."
        }) {
            repository.setPassword(s.password)
            _state.update { it.copy(step = SignupStep.IDENTITY, password = "", passwordConfirm = "", isBusy = false) }
        }
    }

    private fun sendIdentityCode() {
        val s = _state.value
        val f = s.identity
        if (!f.isComplete || s.isBusy) return
        busy {
            when (val r = repository.requestIdentity(f.name.trim(), f.birth, f.genderDigit, f.carrier.label, f.phone)) {
                is IdentityStart.Sent -> _state.update {
                    it.copy(
                        step = SignupStep.IDENTITY_CODE,
                        identityRequestId = r.requestId,
                        identityCode = "",
                        // 남은 시간은 폰 시계로 센다 (서버 시계와 몇 초 달라도 3분 그대로). 진짜 만료는 서버가 판단
                        identityDeadline = now() + IDENTITY_CODE_MS,
                        devCode = r.devCode,
                        isBusy = false,
                        message = if (s.step == SignupStep.IDENTITY_CODE) "인증번호를 다시 보냈어요." else null,
                    )
                }
                is IdentityStart.AlreadyRegistered -> _state.update { it.copy(existingAccount = r.account, isBusy = false) }
                is IdentityStart.Invalid -> _state.update { it.copy(isBusy = false, fieldError = invalidIdentityText(r.code)) }
                is IdentityStart.NotAvailable -> _state.update { it.copy(isBusy = false, message = "본인인증 서비스를 준비 중이에요.") }
            }
        }
    }

    private fun verifyIdentityCode() {
        val s = _state.value
        val requestId = s.identityRequestId ?: return
        if (s.identityCode.length != 6 || s.isBusy) return
        if (now() > s.identityDeadline) {
            _state.update { it.copy(fieldError = "인증번호가 만료됐어요. 다시 받아 주세요.") }
            return
        }
        busy {
            when (val r = repository.verifyIdentity(requestId, s.identityCode)) {
                is IdentityVerify.Verified -> _state.update {
                    it.copy(step = SignupStep.HANDLE, verifiedName = r.name, devCode = null, isBusy = false)
                }
                is IdentityVerify.Wrong -> _state.update { it.copy(isBusy = false, fieldError = "인증번호가 맞지 않아요.") }
                is IdentityVerify.TooManyAttempts, is IdentityVerify.Expired ->
                    _state.update { it.copy(isBusy = false, fieldError = "인증번호가 만료됐어요. 다시 받아 주세요.") }
                is IdentityVerify.AlreadyRegistered -> _state.update { it.copy(existingAccount = r.account, isBusy = false) }
            }
        }
    }

    private fun useExistingAccount() {
        val account = _state.value.existingAccount ?: return
        busy(onError = { "잠시 후 다시 시도해 주세요. 방금 만든 계정을 정리하지 못했어요." }) {
            repository.abandon()
            // 이메일 계정이면 기존 로그인 화면, 소셜이면 시작 화면의 그 버튼으로
            _state.value = SignupState(
                gate = SignupGate.SIGNUP,
                showLogin = account.provider == "email",
                message = if (account.provider == "email") null else "${providerLabel(account.provider)}로 로그인해 주세요.",
            )
        }
    }

    private fun updateHandle(raw: String) {
        val handle = raw.trim().removePrefix("@").lowercase()
        handleJob?.cancel()
        if (!HANDLE_RULE.matches(handle)) {
            _state.update { it.copy(handle = handle, handleCheck = if (handle.isEmpty()) HandleCheck.IDLE else HandleCheck.INVALID) }
            return
        }
        _state.update { it.copy(handle = handle, handleCheck = HandleCheck.CHECKING) }
        handleJob = viewModelScope.launch {
            delay(HANDLE_CHECK_DEBOUNCE_MS)
            val status = runCatching { repository.checkHandle(handle) }.getOrNull()
            // 그 사이 다른 아이디를 입력했으면 버린다 (취소로 대부분 걸러지지만 마지막 확인)
            if (_state.value.handle != handle) return@launch
            _state.update {
                it.copy(
                    handleCheck = when (status) {
                        HandleStatus.AVAILABLE -> HandleCheck.AVAILABLE
                        HandleStatus.TAKEN -> HandleCheck.TAKEN
                        HandleStatus.INVALID -> HandleCheck.INVALID
                        null -> HandleCheck.IDLE
                    },
                )
            }
        }
    }

    private fun submitHandle() {
        val s = _state.value
        if (s.handleCheck != HandleCheck.AVAILABLE) return
        _state.update { it.copy(step = SignupStep.PURPOSE) }
    }

    /** 목적까지 고르면 가입을 마친다 (아이디·목적 저장 + 사진). 그다음 알림 안내. */
    private fun submitPurpose() {
        val s = _state.value
        val purpose = s.purpose ?: return
        if (s.isBusy) return
        busy {
            if (repository.complete(s.handle, purpose.code, s.avatar)) {
                _state.update { it.copy(step = SignupStep.NOTIFICATION, isBusy = false) }
            } else {
                _state.update {
                    it.copy(step = SignupStep.HANDLE, handleCheck = HandleCheck.TAKEN, isBusy = false, message = "그 사이 다른 사람이 쓴 아이디예요.")
                }
            }
        }
    }

    private fun abandon() {
        _state.update { it.copy(showAbandonConfirm = false) }
        busy(onError = { "가입을 그만두지 못했어요. 잠시 후 다시 시도해 주세요." }) {
            repository.abandon()
            _state.value = SignupState(gate = SignupGate.SIGNUP)
        }
    }

    // ---------- 도우미 ----------

    /** 요청 하나: 중복 실행 막고, 실패하면 안내. [onFieldError] 가 문구를 주면 입력칸 아래에. */
    private fun busy(
        onError: (Throwable) -> String? = { "잠시 후 다시 시도해 주세요." },
        onFieldError: (Throwable) -> String? = { null },
        finally: () -> Unit = {},
        block: suspend () -> Unit,
    ) {
        _state.update { it.copy(isBusy = true, fieldError = null) }
        viewModelScope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isBusy = false, message = onError(e), fieldError = onFieldError(e)) }
            } finally {
                finally()
            }
        }
    }

    private fun now() = Clock.System.now().toEpochMilliseconds()

    private fun invalidIdentityText(code: String) = when (code) {
        "invalid_name" -> "이름을 확인해 주세요."
        "invalid_birth" -> "생년월일 6자리를 확인해 주세요."
        "invalid_gender" -> "주민번호 뒷자리 첫 숫자를 확인해 주세요."
        "invalid_phone" -> "휴대폰 번호를 확인해 주세요."
        else -> "입력한 정보를 확인해 주세요."
    }

    private sealed interface Back {
        object Stay : Back
        object Abandon : Back
        object Finish : Back
        data class To(val step: SignupStep) : Back
    }
}

fun providerLabel(provider: String) = when (provider) {
    "kakao" -> "카카오"
    "naver" -> "네이버"
    "google" -> "Google"
    else -> "이메일"
}
