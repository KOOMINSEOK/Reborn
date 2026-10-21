package com.gentlelady.reborn.myprofile

import com.gentlelady.reborn.core.network.ImageUploader
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.myprofile.data.MyProfileRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MyProfileRepositoryTest {

    private val json = headersOf(HttpHeaders.ContentType, "application/json")
    private val profile = """{"id":"u","handle":"me","displayName":"나","isPrivate":false,"followerCount":1,"followingCount":2,
        "avatarUrl":"https://storage.test/img/0.jpg"}"""

    private fun api(engine: MockEngine) = RebornApi(
        HttpClient(engine) {
            install(ContentNegotiation) { json() }
            defaultRequest { url("http://api.test/"); contentType(ContentType.Application.Json) }
        },
        uploadClient = HttpClient(engine),
    )

    @Test
    fun uploadsPhotoThenPatchesAvatarUrl() = runTest {
        var patchBody: String? = null
        val engine = MockEngine { req ->
            when {
                req.method == HttpMethod.Post && req.url.encodedPath.endsWith("/uploads") ->
                    respond("""{"uploadUrl":"https://storage.test/put/0","imageUrl":"https://storage.test/img/0.jpg"}""", headers = json)
                req.method == HttpMethod.Put -> respond("{}", headers = json)
                req.method == HttpMethod.Patch && req.url.encodedPath.endsWith("/me/profile") -> {
                    patchBody = req.body.toByteArray().decodeToString()
                    respond(profile, headers = json)
                }
                else -> error("unexpected ${req.method} ${req.url}")
            }
        }
        val api = api(engine)

        val updated = MyProfileRepositoryImpl(api, ImageUploader(api)).updateAvatar(byteArrayOf(1))

        val sent = Json.parseToJsonElement(patchBody!!).jsonObject
        assertEquals("https://storage.test/img/0.jpg", sent.getValue("avatarUrl").jsonPrimitive.content)
        assertEquals("https://storage.test/img/0.jpg", updated.avatarUrl)
    }

    @Test
    fun profileIsNullWhenServerFails() = runTest {
        val api = api(MockEngine { respond("nope", HttpStatusCode.Unauthorized) })
        assertNull(MyProfileRepositoryImpl(api, ImageUploader(api)).getMyProfile())
    }

    @Test
    fun myPostsBecomeFeedCards() = runTest {
        var path: String? = null
        val feed = """{"items":[
            {"id":"p1","authorId":"u","authorName":"나","authorHandle":"me","caption":"첫 줄 제목
둘째 줄 본문","imageUrls":["/static/a.png","/static/b.png"],
             "isPosthumous":false,"status":"published","likeCount":3,"commentCount":1,"createdAt":"2026-10-20T10:00:00Z"},
            {"id":"p2","authorId":"u","authorName":"나","authorHandle":"me","caption":"한 줄만","imageUrls":[],
             "isPosthumous":false,"status":"published","likeCount":0,"commentCount":0,"createdAt":"2026-10-19T10:00:00Z"}]}"""
        val api = api(MockEngine { req -> path = req.url.encodedPath + "?" + req.url.encodedQuery; respond(feed, headers = json) })

        val cards = MyProfileRepositoryImpl(api, ImageUploader(api)).myPosts("u", 10)

        assertEquals("/users/u/posts?offset=0&limit=10", path)
        assertEquals(listOf("첫 줄 제목", "한 줄만"), cards.map { it.title })
        assertEquals(listOf("둘째 줄 본문", "2026-10-19"), cards.map { it.subtitle }) // 본문이 한 줄이면 날짜
        assertEquals("/static/a.png", cards[0].imageUrl!!.substringAfter("8080")) // 첫 사진, 서버 주소 붙음
        assertNull(cards[1].imageUrl)
        assertEquals(3, cards[0].likes)
    }
}
