package com.gentlelady.reborn.signup

import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.core.auth.Session
import com.gentlelady.reborn.core.auth.SessionStore
import com.gentlelady.reborn.core.network.dto.SignupStateDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SignupViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    /** 서버 흉내 — 세션과 가입 상태를 들고 있다 */
    private class FakeRepo(val store: SessionStore) : SignupRepository {
        var server = SignupStateDto(termsAgreed = false, identityVerified = false, hasPassword = false, provider = "email", onboarded = false)
        val calls = mutableListOf<String>()
        var identityResult: IdentityStart = IdentityStart.Sent("req", "2026-10-21T00:00:00Z", "123456")
        var completeResult = true
        val checked = mutableListOf<String>()

        override suspend fun state() = server.also { calls += "state" }
        override suspend fun requestEmailCode(email: String) { calls += "otp" }
        override suspend fun verifyEmailCode(email: String, code: String) {
            calls += "verify"
            store.current = Session("AT", "RT", 0, userId = "new-user")
        }
        override suspend fun setPassword(password: String) { calls += "password"; server = server.copy(hasPassword = true) }
        override suspend fun agreeTerms(marketing: Boolean) { calls += "terms:$marketing"; server = server.copy(termsAgreed = true) }
        override suspend fun requestIdentity(name: String, birth: String, genderDigit: String, carrier: String, phone: String) =
            identityResult.also { calls += "identity" }
        override suspend fun verifyIdentity(requestId: String, code: String): IdentityVerify =
            if (code == "123456") IdentityVerify.Verified("이윤주") else IdentityVerify.Wrong
        override suspend fun checkHandle(handle: String) = HandleStatus.AVAILABLE.also { checked += handle }
        override suspend fun complete(handle: String, purpose: String, avatar: ByteArray?) = completeResult.also { calls += "complete:$handle:$purpose" }
        var oauthCodes = mutableListOf<String>()
        override suspend fun oauthUrl(provider: String) = "https://login/$provider"
        override suspend fun completeOAuth(code: String) { oauthCodes += code; store.current = Session("AT", "RT", 0, userId = "social-user") }
        override suspend fun naverLogin(accessToken: String) { calls += "naver:$accessToken"; store.current = Session("AT", "RT", 0, userId = "naver-user") }
        var abandonFails = false
        override suspend fun abandon() {
            calls += "abandon"
            if (abandonFails) error("server down")
            store.current = null
        }
    }

    private fun setup(session: Boolean = false): Triple<SignupViewModel, FakeRepo, SessionStore> {
        val store = SessionStore().apply { if (session) current = Session("AT", "RT", 0, userId = "u") }
        val repo = FakeRepo(store)
        return Triple(SignupViewModel(repo, CurrentUserIdUseCase(store)), repo, store)
    }

    @Test
    fun emailFlowBeforeAccountAndBackNavigation() = runTest(dispatcher) {
        val (vm, _, _) = setup()
        runCurrent()
        assertEquals(SignupGate.SIGNUP, vm.state.value.gate)
        assertEquals(SignupStep.START, vm.state.value.step)

        vm.onIntent(SignupIntent.Start(SignupMethod.EMAIL, "email"))
        vm.onIntent(SignupIntent.SubmitTerms) // 필수 동의 전엔 안 넘어감
        assertEquals(SignupStep.TERMS, vm.state.value.step)
        vm.onIntent(SignupIntent.UpdateTerms(TermsForm(age = true, service = true, privacy = true)))
        vm.onIntent(SignupIntent.SubmitTerms)
        assertEquals(SignupStep.EMAIL, vm.state.value.step) // 계정이 없으니 서버엔 아직 안 보냄

        vm.onIntent(SignupIntent.Back)
        assertEquals(SignupStep.TERMS, vm.state.value.step)
        vm.onIntent(SignupIntent.Back)
        assertEquals(SignupStep.START, vm.state.value.step)
    }

    @Test
    fun verifyingEmailCodeCreatesAccountSendsTermsThenAsksPassword() = runTest(dispatcher) {
        val (vm, repo, _) = setup()
        runCurrent()
        vm.onIntent(SignupIntent.Start(SignupMethod.EMAIL, "email"))
        vm.onIntent(SignupIntent.UpdateTerms(TermsForm(age = true, service = true, privacy = true, marketing = true)))
        vm.onIntent(SignupIntent.SubmitTerms)
        vm.onIntent(SignupIntent.UpdateEmail("yunju@naver.com"))
        vm.onIntent(SignupIntent.SendEmailCode)
        runCurrent()
        assertEquals(SignupStep.EMAIL_CODE, vm.state.value.step)

        vm.onIntent(SignupIntent.UpdateEmailCode("381234"))
        vm.onIntent(SignupIntent.VerifyEmailCode)
        runCurrent()
        // 세션이 생긴 순간 자동 이어하기가 끼어들지 않고, 약관을 먼저 보낸 뒤 상태를 묻는다
        assertEquals(listOf("otp", "verify", "terms:true", "state"), repo.calls)
        assertEquals(SignupStep.PASSWORD, vm.state.value.step)

        vm.onIntent(SignupIntent.Back) // 계정이 생긴 뒤 첫 화면 → 그만두기 확인
        assertTrue(vm.state.value.showAbandonConfirm)
        vm.onIntent(SignupIntent.ConfirmAbandon)
        runCurrent()
        assertTrue("abandon" in repo.calls)
        assertEquals(SignupStep.START, vm.state.value.step)
    }

    @Test
    fun appStartResumesAtFirstMissingStep() = runTest(dispatcher) {
        val (vm, repo, store) = setup(session = true)
        repo.server = repo.server.copy(termsAgreed = true, hasPassword = true, identityVerified = true, identityName = "이윤주")
        runCurrent()
        assertEquals(SignupStep.HANDLE, vm.state.value.step)
        assertEquals("이윤주", vm.state.value.verifiedName)

        // 가입 끝난 계정으로 바꿔 로그인하면 앱으로
        repo.server = repo.server.copy(onboarded = true)
        store.current = Session("AT2", "RT2", 0, userId = "other")
        runCurrent()
        assertEquals(SignupGate.DONE, vm.state.value.gate)
    }

    @Test
    fun alreadyRegisteredEmailAccountGoesToLoginAfterAbandoningNewOne() = runTest(dispatcher) {
        val (vm, repo, _) = setup(session = true)
        repo.server = repo.server.copy(termsAgreed = true, hasPassword = true)
        runCurrent()
        assertEquals(SignupStep.IDENTITY, vm.state.value.step)
        repo.identityResult = IdentityStart.AlreadyRegistered(ExistingAccount("email", "yunju_reborn", "2026-09-01T00:00:00Z"))
        vm.onIntent(SignupIntent.UpdateIdentity(IdentityForm("이윤주", "980315", "2", Carrier.SKT, "01012345678", agreed = true)))
        vm.onIntent(SignupIntent.SendIdentityCode)
        runCurrent()
        assertEquals("yunju_reborn", vm.state.value.existingAccount?.handle)

        vm.onIntent(SignupIntent.UseExistingAccount)
        runCurrent()
        assertTrue("abandon" in repo.calls) // 방금 만든 빈 계정은 지운다
        assertTrue(vm.state.value.showLogin) // 이메일 계정 → 로그인 화면
    }

    @Test
    fun identityCodeWrongThenRightGoesToHandle() = runTest(dispatcher) {
        val (vm, repo, _) = setup(session = true)
        repo.server = repo.server.copy(termsAgreed = true, hasPassword = true)
        runCurrent()
        vm.onIntent(SignupIntent.UpdateIdentity(IdentityForm("이윤주", "980315", "2", Carrier.SKT, "01012345678", agreed = true)))
        vm.onIntent(SignupIntent.SendIdentityCode)
        runCurrent()
        assertEquals("123456", vm.state.value.devCode)

        vm.onIntent(SignupIntent.UpdateIdentityCode("000000"))
        vm.onIntent(SignupIntent.VerifyIdentityCode)
        runCurrent()
        assertEquals("인증번호가 맞지 않아요.", vm.state.value.fieldError)
        vm.onIntent(SignupIntent.UpdateIdentityCode("123456"))
        vm.onIntent(SignupIntent.VerifyIdentityCode)
        runCurrent()
        assertEquals(SignupStep.HANDLE, vm.state.value.step)
        assertEquals("이윤주", vm.state.value.verifiedName)
    }

    @Test
    fun handleCheckIsDebouncedAndRulesCheckedLocally() = runTest(dispatcher) {
        val (vm, repo, _) = setup(session = true)
        runCurrent()
        vm.onIntent(SignupIntent.UpdateHandle("AB"))
        assertEquals(HandleCheck.INVALID, vm.state.value.handleCheck) // 서버에 안 묻는다 (소문자로 바꿔도 4자 미만)
        vm.onIntent(SignupIntent.UpdateHandle("yunj"))
        vm.onIntent(SignupIntent.UpdateHandle("yunju_reborn"))
        assertEquals(HandleCheck.CHECKING, vm.state.value.handleCheck)
        advanceTimeBy(500)
        runCurrent()
        assertEquals(listOf("yunju_reborn"), repo.checked) // 마지막 것만
        assertEquals(HandleCheck.AVAILABLE, vm.state.value.handleCheck)
    }

    @Test
    fun handleTakenAtCompletionReturnsToHandleStep() = runTest(dispatcher) {
        val (vm, repo, _) = setup(session = true)
        repo.server = repo.server.copy(termsAgreed = true, hasPassword = true, identityVerified = true)
        runCurrent()
        vm.onIntent(SignupIntent.UpdateHandle("yunju_reborn"))
        advanceTimeBy(500)
        runCurrent()
        vm.onIntent(SignupIntent.SubmitHandle)
        vm.onIntent(SignupIntent.SelectPurpose(SignupPurpose.REMEMBER))
        repo.completeResult = false
        vm.onIntent(SignupIntent.SubmitPurpose)
        runCurrent()
        assertEquals(SignupStep.HANDLE, vm.state.value.step)
        assertEquals(HandleCheck.TAKEN, vm.state.value.handleCheck)

        repo.completeResult = true
        vm.onIntent(SignupIntent.UpdateHandle("yunju_2"))
        advanceTimeBy(500)
        runCurrent()
        vm.onIntent(SignupIntent.SubmitHandle)
        vm.onIntent(SignupIntent.SubmitPurpose)
        runCurrent()
        assertTrue("complete:yunju_2:remember" in repo.calls)
        assertEquals(SignupStep.NOTIFICATION, vm.state.value.step)
        vm.onIntent(SignupIntent.Finish)
        assertEquals(SignupGate.DONE, vm.state.value.gate)
    }

    @Test
    fun socialButtonsExplainNotReadyYet() = runTest(dispatcher) {
        val (vm, _, _) = setup()
        runCurrent()
        vm.onIntent(SignupIntent.Start(SignupMethod.SOCIAL, "kakao"))
        assertEquals(SignupStep.START, vm.state.value.step)
        assertFalse(vm.state.value.message.isNullOrBlank())
    }

    @Test
    fun passwordRules() {
        assertFalse(PasswordRules.of("abc12").ok) // 8자 미만
        assertFalse(PasswordRules.of("abcdefgh").ok) // 한 가지뿐
        assertTrue(PasswordRules.of("abcdefg1").ok)
        assertTrue(PasswordRules.of("12345678!").ok)
        assertTrue(PasswordRules.of("비밀번호!!!!!").ok) // 한글도 "문자"
    }

    @Test
    fun failedAbandonKeepsSessionAndExplains() = runTest(dispatcher) {
        val (vm, repo, store) = setup(session = true)
        repo.server = repo.server.copy(termsAgreed = true, hasPassword = true)
        runCurrent()
        repo.abandonFails = true
        vm.onIntent(SignupIntent.Back)
        vm.onIntent(SignupIntent.ConfirmAbandon)
        runCurrent()
        assertTrue(store.current != null) // 서버가 못 지웠으면 로그아웃도 안 한다
        assertEquals(SignupStep.IDENTITY, vm.state.value.step)
        assertEquals("가입을 그만두지 못했어요. 잠시 후 다시 시도해 주세요.", vm.state.value.message)
    }

    private class FakeLauncher(var naverToken: String?) : com.gentlelady.reborn.core.auth.SocialLoginLauncher {
        val opened = mutableListOf<String>()
        val naverAsks = mutableListOf<Boolean>()
        override fun openBrowser(url: String) { opened += url }
        override suspend fun naverAccessToken(otherAccount: Boolean) = naverToken.also { naverAsks += otherAccount }
    }

    @Test
    fun kakaoOpensBrowserThenRedirectSignsInAndResumesAtTerms() = runTest(dispatcher) {
        val store = SessionStore()
        val repo = FakeRepo(store).apply { server = server.copy(provider = "kakao", hasPassword = true) }
        val launcher = FakeLauncher(null)
        val redirects = kotlinx.coroutines.flow.MutableSharedFlow<String>()
        val vm = SignupViewModel(repo, CurrentUserIdUseCase(store), launcher, redirects)
        runCurrent()
        vm.onIntent(SignupIntent.Start(SignupMethod.SOCIAL, "kakao"))
        runCurrent()
        assertEquals(listOf("https://login/kakao"), launcher.opened)
        assertFalse(vm.state.value.isBusy)

        redirects.emit("com.gentlelady.reborn://login-callback?error=access_denied")
        runCurrent()
        assertTrue(store.current == null)
        redirects.emit("com.gentlelady.reborn://login-callback?code=abc")
        runCurrent()
        assertEquals(listOf("abc"), repo.oauthCodes)
        assertEquals(SignupStep.TERMS, vm.state.value.step) // 새 사람 → 약관부터
        assertEquals(SignupMethod.SOCIAL, vm.state.value.method)
        assertFalse(vm.state.value.isBusy)
    }

    @Test
    fun naverCancelStaysAndSignedUpUserGoesStraightIn() = runTest(dispatcher) {
        val store = SessionStore()
        val repo = FakeRepo(store)
        val launcher = FakeLauncher(null)
        val vm = SignupViewModel(repo, CurrentUserIdUseCase(store), launcher, kotlinx.coroutines.flow.emptyFlow())
        runCurrent()
        vm.onIntent(SignupIntent.Start(SignupMethod.SOCIAL, "naver"))
        assertTrue(vm.state.value.showNaverChooser) // 이 기기 계정 / 다른 계정 고르기
        vm.onIntent(SignupIntent.NaverLogin(otherAccount = false))
        runCurrent()
        assertFalse(vm.state.value.showNaverChooser)
        assertEquals(SignupStep.START, vm.state.value.step) // 창을 닫음 → 그대로
        assertFalse(vm.state.value.isBusy)

        launcher.naverToken = "NT"
        repo.server = repo.server.copy(provider = "naver", onboarded = true)
        vm.onIntent(SignupIntent.Start(SignupMethod.SOCIAL, "naver"))
        vm.onIntent(SignupIntent.NaverLogin(otherAccount = true))
        runCurrent()
        assertEquals(listOf(false, true), launcher.naverAsks)
        assertTrue("naver:NT" in repo.calls)
        assertEquals(SignupGate.DONE, vm.state.value.gate) // 이미 가입한 사람 → 바로 앱
    }
}
