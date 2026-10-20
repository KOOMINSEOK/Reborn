package com.gentlelady.reborn.server.profile

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProfileTextTest {

    @Test
    fun acceptsTrimmedWithinLimits() {
        assertNull(profileTextError(UpdateProfileRequest(displayName = "  홍길동  ", bio = "안녕하세요")))
        assertNull(profileTextError(UpdateProfileRequest(displayName = "가".repeat(MAX_DISPLAY_NAME))))
        assertNull(profileTextError(UpdateProfileRequest(bio = ""))) // 소개 비우기는 허용
        assertNull(profileTextError(UpdateProfileRequest(avatarUrl = "x"))) // 글자 필드 없으면 검사 안 함
    }

    @Test
    fun rejectsEmptyOrTooLong() {
        assertEquals("empty_display_name", profileTextError(UpdateProfileRequest(displayName = "   ")))
        assertEquals("display_name_too_long", profileTextError(UpdateProfileRequest(displayName = "가".repeat(MAX_DISPLAY_NAME + 1))))
        assertEquals("bio_too_long", profileTextError(UpdateProfileRequest(bio = "가".repeat(MAX_BIO + 1))))
    }
}
