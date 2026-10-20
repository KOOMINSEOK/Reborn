package com.gentlelady.reborn.server.memorial

import java.time.OffsetDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GuestbookCursorTest {

    @Test
    fun roundTripsWithMicrosecondsAndOffset() {
        val cursor = GuestbookCursor(
            OffsetDateTime.parse("2026-10-20T07:15:51.123456+09:00"),
            UUID.fromString("2f09715f-687a-478b-bea2-afd1833066d9"),
        )
        assertEquals(cursor, GuestbookCursor.parse(cursor.toString()))
    }

    @Test
    fun rejectsMalformedCursor() {
        assertFailsWith<IllegalArgumentException> { GuestbookCursor.parse("nope") }
        assertFailsWith<IllegalArgumentException> { GuestbookCursor.parse("2026-10-20T00:00:00Z_not-a-uuid") }
    }
}
