package com.gentlelady.reborn.core.network

import com.gentlelady.reborn.core.network.dto.toAbsoluteServerUrl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PostMappersTest {

    @Test
    fun prefixesRelativePathsWithBaseUrl() {
        assertEquals(ApiConfig.BASE_URL + "/static/seed/x.png", "/static/seed/x.png".toAbsoluteServerUrl())
    }

    @Test
    fun leavesAbsoluteUrlsAndNullUntouched() {
        assertEquals("https://cdn.example.com/x.png", "https://cdn.example.com/x.png".toAbsoluteServerUrl())
        assertNull(null.toAbsoluteServerUrl())
    }
}
