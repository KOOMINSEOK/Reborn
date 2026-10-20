package com.gentlelady.reborn.server.upload

import com.gentlelady.reborn.server.module
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StorageTest {

    private val base = "https://proj.supabase.co"
    private val me = UUID.fromString("00000000-0000-0000-0000-0000000000a1")
    private val other = UUID.fromString("00000000-0000-0000-0000-0000000000a2")
    private val file = "0f8fad5b-d9cb-469f-a165-70867728950e"

    private fun url(owner: UUID, name: String) = "$base/storage/v1/object/public/images/$owner/$name"

    @Test
    fun acceptsOwnUploadedImage() {
        assertTrue(isOwnImageUrl(base, me, url(me, "$file.jpg")))
        assertTrue(isOwnImageUrl(base, me, url(me, "$file.webp")))
    }

    @Test
    fun rejectsOtherUsersFolderForeignHostAndTraversal() {
        assertFalse(isOwnImageUrl(base, me, url(other, "$file.jpg")))
        assertFalse(isOwnImageUrl(base, me, "https://evil.example/storage/v1/object/public/images/$me/$file.jpg"))
        assertFalse(isOwnImageUrl(base, me, url(me, "../$other/$file.jpg")))
        assertFalse(isOwnImageUrl(base, me, url(me, "$file.gif")))
        assertFalse(isOwnImageUrl(base, me, url(me, "$file.jpg?x=1")))
        assertFalse(isOwnImageUrl(base, me, "/static/seed/img_post_dummy1.png"))
    }

    @Test
    fun imageListErrorChecksCountBeforeOwnership() {
        // 테스트 환경은 SUPABASE_URL 미설정 → 어떤 URL 도 내 이미지로 인정되지 않는다
        assertEquals(null, imageListError(me, emptyList()))
        assertEquals("invalid_image_url", imageListError(me, listOf(url(me, "$file.jpg"))))
        assertEquals("too_many_images", imageListError(me, List(MAX_IMAGES + 1) { url(me, "$file.jpg") }))
    }

    @Test
    fun uploadEndpointRequiresAuth() = testApplication {
        application { module() }
        assertEquals(HttpStatusCode.Unauthorized, client.post("/uploads").status)
    }
}
