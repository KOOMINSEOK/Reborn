package com.gentlelady.reborn.feedwrite

import com.gentlelady.reborn.core.network.ImageUploader
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.feedwrite.data.repository.FeedWriteRepositoryImpl
import com.gentlelady.reborn.feedwrite.domain.repository.FeedWriteRepository
import com.gentlelady.reborn.feedwrite.domain.usecase.CreatePostUseCase
import com.gentlelady.reborn.feedwrite.presentation.FeedPostType
import com.gentlelady.reborn.feedwrite.presentation.FeedWriteIntent
import com.gentlelady.reborn.feedwrite.presentation.FeedWriteReducer
import com.gentlelady.reborn.feedwrite.presentation.FeedWriteResult
import com.gentlelady.reborn.feedwrite.presentation.FeedWriteState
import com.gentlelady.reborn.feedwrite.presentation.MAX_POST_IMAGES
import com.gentlelady.reborn.feedwrite.presentation.ScheduleOption
import com.gentlelady.reborn.feedwrite.presentation.publishAt
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
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FeedWriteTest {

    private val json = headersOf(HttpHeaders.ContentType, "application/json")

    @Test
    fun uploadsEveryImageThenCreatesPostWithUrlsInOrder() = runTest {
        var uploadSeq = 0
        val puts = mutableListOf<Pair<String, ByteArray>>()
        var postBody: String? = null

        val engine = MockEngine { req ->
            when {
                req.method == HttpMethod.Post && req.url.encodedPath.endsWith("/uploads") -> {
                    val n = uploadSeq++
                    respond("""{"uploadUrl":"https://storage.test/put/$n","imageUrl":"https://storage.test/img/$n.jpg"}""", headers = json)
                }
                req.method == HttpMethod.Put -> {
                    puts += req.url.toString() to req.body.toByteArray()
                    respond("{}", headers = json)
                }
                req.method == HttpMethod.Post && req.url.encodedPath.endsWith("/posts") -> {
                    postBody = req.body.toByteArray().decodeToString()
                    respond(
                        """{"id":"p","authorId":"a","authorName":"n","authorHandle":"h","caption":"c",
                        "isPosthumous":true,"status":"scheduled","likeCount":0,"commentCount":0,"createdAt":"2026-10-20T00:00:00Z"}""",
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
        val images = listOf(byteArrayOf(1), byteArrayOf(2), byteArrayOf(3))
        val at = Instant.parse("2026-10-27T00:00:00Z")

        val repo = FeedWriteRepositoryImpl(api, ImageUploader(api))
        val uploaded = repo.uploadImages(images)
        repo.createPost("안녕", uploaded, isPosthumous = true, publishAt = at, clientRequestId = "req-1")

        assertEquals(3, puts.size)
        // 업로드 URL n 에는 n 번째로 발급받은 이미지가 올라가야 하고, 글에는 사진 순서대로 imageUrl 이 들어간다
        val body = Json.parseToJsonElement(postBody!!).jsonObject
        val urls = body.getValue("imageUrls").jsonArray.map { it.jsonPrimitive.content }
        assertEquals(3, urls.size)
        urls.forEachIndexed { i, url ->
            val n = url.substringAfterLast('/').substringBefore('.')
            assertEquals(images[i].toList(), puts.first { it.first.endsWith("/put/$n") }.second.toList())
        }
        assertEquals("2026-10-27T00:00:00Z", body.getValue("publishAt").jsonPrimitive.content)
        assertEquals("req-1", body.getValue("clientRequestId").jsonPrimitive.content)
    }

    @Test
    fun retryReusesUploadedImagesAndRequestId() = runTest {
        val repo = object : FeedWriteRepository {
            var uploads = 0
            var postAttempts = 0
            val requestIds = mutableListOf<String>()
            override suspend fun uploadImages(images: List<ByteArray>): List<String> {
                uploads++
                return images.indices.map { "https://img/$it.jpg" }
            }
            override suspend fun createPost(
                caption: String, imageUrls: List<String>, isPosthumous: Boolean, publishAt: Instant?, clientRequestId: String,
            ) {
                requestIds += clientRequestId
                if (postAttempts++ == 0) error("응답 유실")
            }
        }
        val useCase = CreatePostUseCase(repo)
        var state = FeedWriteReducer.reduce(FeedWriteState(), FeedWriteIntent.AddImage(byteArrayOf(1)))

        suspend fun submit() = FeedWriteReducer.reduce(
            state,
            useCase(state.caption, state.images, state.uploadedImageUrls, false, null, state.requestId),
        )
        state = submit()
        assertEquals(listOf("https://img/0.jpg"), state.uploadedImageUrls)
        state = submit()

        assertTrue(state.isSubmitted)
        assertEquals(1, repo.uploads)
        assertEquals(2, repo.requestIds.size)
        assertEquals(repo.requestIds[0], repo.requestIds[1])
    }

    @Test
    fun editsAreIgnoredWhileSubmittingAndRotateRequestIdOtherwise() {
        val submitting = FeedWriteReducer.reduce(FeedWriteState(caption = "a"), FeedWriteResult.Submitting)
        assertEquals(submitting, FeedWriteReducer.reduce(submitting, FeedWriteIntent.ChangeCaption("b")))
        assertEquals(submitting, FeedWriteReducer.reduce(submitting, FeedWriteIntent.AddImage(byteArrayOf(1))))

        val idle = FeedWriteState(caption = "a")
        assertTrue(FeedWriteReducer.reduce(idle, FeedWriteIntent.ChangeCaption("b")).requestId != idle.requestId)
    }

    @Test
    fun changingImagesDropsUploadedUrls() {
        val failed = FeedWriteReducer.reduce(FeedWriteState(), FeedWriteResult.SubmitFailed("x", listOf("u")))
        assertNull(FeedWriteReducer.reduce(failed, FeedWriteIntent.RemoveImage(0)).uploadedImageUrls)
        assertNull(FeedWriteReducer.reduce(failed, FeedWriteIntent.AddImage(byteArrayOf(1))).uploadedImageUrls)
    }

    @Test
    fun reducerCapsImagesAtMax() {
        var state = FeedWriteState()
        repeat(MAX_POST_IMAGES + 3) { state = FeedWriteReducer.reduce(state, FeedWriteIntent.AddImage(byteArrayOf(it.toByte()))) }
        assertEquals(MAX_POST_IMAGES, state.images.size)
        state = FeedWriteReducer.reduce(state, FeedWriteIntent.RemoveImage(0))
        assertEquals(1.toByte(), state.images.first()[0])
    }

    @Test
    fun publishAtFollowsScheduleAndIsNullForLivingPosts() {
        val now = Instant.parse("2026-10-20T03:00:00Z")
        val utc = TimeZone.UTC
        assertNull(FeedWriteState().publishAt(FeedPostType.LIVING, now, utc))
        assertEquals(
            Instant.parse("2026-10-21T03:00:00Z"),
            FeedWriteState(scheduleOption = ScheduleOption.ONE_DAY).publishAt(FeedPostType.POSTHUMOUS, now, utc),
        )
        assertEquals(
            Instant.parse("2026-12-25T00:00:00Z"),
            FeedWriteState(scheduleOption = ScheduleOption.CUSTOM, customDate = LocalDate(2026, 12, 25))
                .publishAt(FeedPostType.POSTHUMOUS, now, utc),
        )
    }
}
