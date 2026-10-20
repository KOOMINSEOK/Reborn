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
}
