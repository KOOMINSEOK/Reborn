package com.gentlelady.reborn.memorial

import com.gentlelady.reborn.core.auth.SessionStore
import com.gentlelady.reborn.core.network.ImageUploader
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.memorial.data.MemorialRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class MemorialHistoryWriteTest {

    private val json = headersOf(HttpHeaders.ContentType, "application/json")

    @Test
    fun uploadsPhotoThenCreatesHistoryWithItsUrl() = runTest {
        var putBody: ByteArray? = null
        var historyBody: String? = null
        val engine = MockEngine { req ->
            when {
                req.method == HttpMethod.Post && req.url.encodedPath.endsWith("/uploads") ->
                    respond("""{"uploadUrl":"https://storage.test/put/0","imageUrl":"https://storage.test/img/0.jpg"}""", headers = json)
                req.method == HttpMethod.Put -> {
                    putBody = req.body.toByteArray()
                    respond("{}", headers = json)
                }
                req.method == HttpMethod.Post && req.url.encodedPath.endsWith("/memorials/m1/history") -> {
                    historyBody = req.body.toByteArray().decodeToString()
                    respond(
                        """{"id":"h1","memorialId":"m1","authorId":"a","authorName":"나","caption":"추억",
                        "imageUrls":["https://storage.test/img/0.jpg"],"likeCount":0,"commentCount":0,
                        "createdAt":"2026-10-20T00:00:00Z"}""",
                        headers = json,
                    )
                }
                else -> error("unexpected ${req.method} ${req.url}")
            }
        }
        val apiClient = HttpClient(engine) {
            install(ContentNegotiation) { json() }
            defaultRequest { url("http://api.test/"); contentType(ContentType.Application.Json) }
        }
        val api = RebornApi(apiClient, uploadClient = HttpClient(engine))

        val item = MemorialRepositoryImpl(api, ImageUploader(api), SessionStore()).createHistory("m1", "추억", byteArrayOf(7, 8))

        assertEquals(listOf<Byte>(7, 8), putBody!!.toList())
        val urls = Json.parseToJsonElement(historyBody!!).jsonObject.getValue("imageUrls").jsonArray.map { it.jsonPrimitive.content }
        assertEquals(listOf("https://storage.test/img/0.jpg"), urls)
        assertEquals("h1", item.id)
        assertEquals(listOf("https://storage.test/img/0.jpg"), item.imageUrls)
    }
}
