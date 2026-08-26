package com.gentlelady.reborn.server

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EnvTest {

    @Test
    fun parsesPairsCommentsAndQuotes() {
        val parsed = Env.parse(
            """
            # 주석
            SUPABASE_URL=https://x.supabase.co

            DB_PASSWORD="p@ss:word"
            DB_USER='postgres'
            NOT A PAIR
            """.trimIndent(),
        )

        assertEquals("https://x.supabase.co", parsed["SUPABASE_URL"])
        assertEquals("p@ss:word", parsed["DB_PASSWORD"])
        assertEquals("postgres", parsed["DB_USER"])
        assertNull(parsed["NOT A PAIR"])
        assertEquals(3, parsed.size)
    }
}
