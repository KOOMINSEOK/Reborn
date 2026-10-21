package com.gentlelady.reborn.server.seed

import com.gentlelady.reborn.server.module
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SeedTest {

    @Test
    fun servesBundledSeedImage() = testApplication {
        application { module() }

        val response = client.get("/static/seed/img_post_dummy1.png")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.body<ByteArray>().isNotEmpty())
    }

    @Test
    fun seedDataHasNoNameCollisions() {
        val names = SeedData.users.map { it.name }
        assertEquals(names.size, names.toSet().size)
        SeedData.posts.forEach { assertTrue(it.authorName in SeedData.userIdByName) }
        SeedData.history.forEach { assertTrue(it.authorName in SeedData.userIdByName) }
    }

    @Test
    fun generatedSeedIsConsistent() {
        val ids = SeedData.users.map { it.id } + SeedData.posts.map { it.id } + SeedData.history.map { it.id } +
            SeedData.guestbook.map { it.id } + SeedData.postComments.map { it.id } + SeedData.historyComments.map { it.id } +
            SeedData.scheduledPosts.map { it.id } + SeedData.memorialId
        assertEquals(ids.size, ids.toSet().size, "시드 id 중복")
        assertEquals(SeedData.users.size, SeedData.users.map { it.handle }.toSet().size, "시드 핸들 중복")
        SeedData.users.forEach { assertTrue(!it.handle.startsWith("user_"), "기본 핸들 패턴과 겹침: ${it.handle}") }

        val names = SeedData.userIdByName.keys
        (SeedData.guestbook.map { it.authorName } + SeedData.postComments.map { it.authorName } +
            SeedData.historyComments.map { it.authorName } + SeedData.memorialFollowers +
            SeedData.follows.flatMap { listOf(it.first, it.second) } +
            SeedData.postLikes.map { it.second } + SeedData.historyLikes.map { it.second })
            .forEach { assertTrue(it in names, "모르는 유저: $it") }
        SeedData.follows.forEach { (a, b) -> assertTrue(a != b, "자기 자신 팔로우") }

        val posts = SeedData.posts.associateBy { it.id }
        SeedData.postLikes.forEach { (id, who) -> assertTrue(posts.getValue(id).authorName != who, "자기 글 좋아요") }
        SeedData.posts.forEach { assertTrue(it.images.size in 1..10) }

        // 무한 스크롤(30개 단위)을 시험할 수 있을 만큼
        assertTrue(SeedData.guestbook.size > 30)
        assertTrue(SeedData.postComments.groupBy { it.parentId }.values.any { it.size > 30 })
    }

    @Test
    fun everySeedImageIsBundled() {
        val files = SeedData.posts.flatMap { it.images } + SeedData.history.map { it.image } +
            SeedData.users.mapNotNull { it.avatar } + SeedData.MEMORIAL_IMAGE
        files.toSet().forEach {
            assertTrue(javaClass.getResource("/static/seed/$it") != null, "번들에 없는 이미지: $it")
        }
    }
}
