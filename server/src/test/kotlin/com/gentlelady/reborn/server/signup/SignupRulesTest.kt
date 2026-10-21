package com.gentlelady.reborn.server.signup

import com.gentlelady.reborn.server.module
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SignupRulesTest {

    @Test
    fun handleRuleMatchesSignupGuide() {
        listOf("yunju_reborn", "a.b_c", "abcd", "x".repeat(20), "user.2026").forEach { assertTrue(HANDLE_RULE.matches(it), it) }
        listOf("abc", "x".repeat(21), "Yunju", "윤주1234", "yun ju", "yun-ju", "").forEach { assertFalse(HANDLE_RULE.matches(it), it) }
    }

    private fun req(name: String = "이윤주", birth: String = "980315", g: String = "2", carrier: String = "SKT", phone: String = "010-1234-5678") =
        IdentityStartRequest(name, birth, g, carrier, phone)

    @Test
    fun identityInputValidation() {
        assertNull(identityInputError(req()))
        assertNull(identityInputError(req(phone = "01012345678")))
        assertEquals("invalid_name", identityInputError(req(name = "이")))
        assertEquals("invalid_birth", identityInputError(req(birth = "98031")))
        assertEquals("invalid_gender", identityInputError(req(g = "5")))
        assertEquals("invalid_carrier", identityInputError(req(carrier = "SK")))
        assertEquals("invalid_phone", identityInputError(req(phone = "02-123-4567")))
    }

    @Test
    fun signupEndpointsRequireAuth() = testApplication {
        application { module() }
        assertEquals(HttpStatusCode.Unauthorized, client.get("/me/signup").status)
        assertEquals(HttpStatusCode.Unauthorized, client.post("/me/identity/request").status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/handles/check?handle=abcd").status)
        assertEquals(HttpStatusCode.Unauthorized, client.delete("/me/signup").status)
    }
}
