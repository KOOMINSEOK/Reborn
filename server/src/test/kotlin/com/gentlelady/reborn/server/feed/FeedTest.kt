package com.gentlelady.reborn.server.feed

import kotlin.test.Test
import kotlin.test.assertEquals

class FeedTest {

    private fun post(id: String, source: String) = PostResponse(
        id = id,
        authorId = "a",
        authorName = "a",
        authorHandle = "a",
        caption = "",
        isPosthumous = false,
        status = "published",
        likeCount = 0,
        commentCount = 0,
        createdAt = "2026-01-01T00:00:00Z",
        source = source,
    )

    @Test
    fun interleavesThreeFollowingThenOneRecommended() {
        val following = (1..7).map { post("f$it", "following") }
        val recommended = (1..3).map { post("r$it", "recommended") }

        val ids = interleave(following, recommended).map { it.id }

        assertEquals(listOf("f1", "f2", "f3", "r1", "f4", "f5", "f6", "r2", "f7", "r3"), ids)
    }

    @Test
    fun keepsRemainingWhenOneSideRunsOut() {
        val ids = interleave(
            following = listOf(post("f1", "following")),
            recommended = (1..3).map { post("r$it", "recommended") },
        ).map { it.id }

        assertEquals(listOf("f1", "r1", "r2", "r3"), ids)
    }
}
