package com.gentlelady.reborn.core.network

import com.gentlelady.reborn.core.network.dto.BlockedListDto
import com.gentlelady.reborn.core.network.dto.CommentDto
import com.gentlelady.reborn.core.network.dto.CompleteSignupDto
import com.gentlelady.reborn.core.network.dto.HandleCheckDto
import com.gentlelady.reborn.core.network.dto.IdentityStartDto
import com.gentlelady.reborn.core.network.dto.IdentityStartResultDto
import com.gentlelady.reborn.core.network.dto.IdentityVerifiedDto
import com.gentlelady.reborn.core.network.dto.IdentityVerifyDto
import com.gentlelady.reborn.core.network.dto.SignupStateDto
import com.gentlelady.reborn.core.network.dto.TermsDto
import com.gentlelady.reborn.core.network.dto.CommentListDto
import com.gentlelady.reborn.core.network.dto.CreateCommentDto
import com.gentlelady.reborn.core.network.dto.CreateGuestbookDto
import com.gentlelady.reborn.core.network.dto.CreateHistoryDto
import com.gentlelady.reborn.core.network.dto.CreatePostDto
import com.gentlelady.reborn.core.network.dto.CreateUploadDto
import com.gentlelady.reborn.core.network.dto.FeedDto
import com.gentlelady.reborn.core.network.dto.FollowDto
import com.gentlelady.reborn.core.network.dto.GuestbookEntryDto
import com.gentlelady.reborn.core.network.dto.GuestbookListDto
import com.gentlelady.reborn.core.network.dto.HistoryDto
import com.gentlelady.reborn.core.network.dto.HistoryListDto
import com.gentlelady.reborn.core.network.dto.LikeDto
import com.gentlelady.reborn.core.network.dto.MemorialProfileDto
import com.gentlelady.reborn.core.network.dto.MyGuestbookListDto
import com.gentlelady.reborn.core.network.dto.MyProfileDto
import com.gentlelady.reborn.core.network.dto.NotificationListDto
import com.gentlelady.reborn.core.network.dto.UnreadCountDto
import com.gentlelady.reborn.core.network.dto.UpdateProfileDto
import com.gentlelady.reborn.core.network.dto.PostDto
import com.gentlelady.reborn.core.network.dto.PublicProfileDto
import com.gentlelady.reborn.core.network.dto.UploadDto
import com.gentlelady.reborn.core.network.dto.UserListDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Reborn 서버 REST 엔드포인트. 호출 실패 시 예외를 던지며, 폴백은 레포지토리 책임.
 * [uploadClient] 는 Auth 플러그인이 없는 클라이언트 — 외부 Storage 서명 URL 에 사용자 토큰이 절대 붙지 않게
 * (Bearer 플러그인은 401 challenge 를 받으면 호스트와 상관없이 토큰을 붙여 재시도한다).
 */
class RebornApi(
    private val client: HttpClient,
    private val uploadClient: HttpClient,
) {

    suspend fun feed(offset: Int = 0, limit: Int = 20): FeedDto =
        client.get("feed") {
            parameter("offset", offset)
            parameter("limit", limit)
        }.body()

    suspend fun post(id: String): PostDto =
        client.get("posts/$id").body()

    suspend fun memorial(id: String): MemorialProfileDto =
        client.get("memorials/$id").body()

    suspend fun memorialHistory(id: String, offset: Int = 0, limit: Int = 30): HistoryListDto =
        client.get("memorials/$id/history") {
            parameter("offset", offset)
            parameter("limit", limit)
        }.body()

    /** 최신순. [cursor] = 지난 응답의 nextCursor (스크롤로 이전 글 이어 읽기). */
    suspend fun guestbook(id: String, cursor: String? = null, limit: Int = GUESTBOOK_PAGE_SIZE): GuestbookListDto =
        client.get("memorials/$id/guestbook") {
            cursor?.let { parameter("cursor", it) }
            parameter("limit", limit)
        }.body()

    /** [entryId] 방명록을 가운데로 앞뒤 글 (최신순). 내 방명록에서 눌러 그 위치로 이동할 때. */
    suspend fun guestbookAround(id: String, entryId: String, limit: Int = GUESTBOOK_PAGE_SIZE): GuestbookListDto =
        client.get("memorials/$id/guestbook") {
            parameter("around", entryId)
            parameter("limit", limit)
        }.body()

    /** [after] 커서보다 최신 글 (최신순). 아래로 스크롤해 이후 글을 이어 읽을 때. */
    suspend fun guestbookNewer(id: String, after: String, limit: Int = GUESTBOOK_PAGE_SIZE): GuestbookListDto =
        client.get("memorials/$id/guestbook") {
            parameter("after", after)
            parameter("limit", limit)
        }.body()

    suspend fun myGuestbook(cursor: String? = null): MyGuestbookListDto =
        client.get("me/guestbook") { cursor?.let { parameter("cursor", it) } }.body()

    suspend fun myProfile(): MyProfileDto = client.get("me/profile").body()

    suspend fun updateMyProfile(req: UpdateProfileDto): MyProfileDto =
        client.patch("me/profile") { setBody(req) }.body()

    suspend fun createUpload(contentType: String): UploadDto =
        client.post("uploads") { setBody(CreateUploadDto(contentType)) }.body()

    /** 서버가 발급한 Supabase Storage 서명 URL 로 이미지 바이트를 직접 올린다 (서버를 거치지 않음). */
    suspend fun uploadImage(uploadUrl: String, jpeg: ByteArray) {
        uploadClient.put(uploadUrl) {
            contentType(ContentType.Image.JPEG)
            setBody(jpeg)
            // 기본 5초는 모바일 망에서 수 MB 사진에 부족하다
            timeout { requestTimeoutMillis = UPLOAD_TIMEOUT_MS }
        }
    }

    suspend fun createPost(req: CreatePostDto): PostDto =
        client.post("posts") { setBody(req) }.body()

    suspend fun createHistory(memorialId: String, req: CreateHistoryDto): HistoryDto =
        client.post("memorials/$memorialId/history") { setBody(req) }.body()

    suspend fun createGuestbookEntry(memorialId: String, req: CreateGuestbookDto): GuestbookEntryDto =
        client.post("memorials/$memorialId/guestbook") { setBody(req) }.body()

    suspend fun userProfile(id: String): PublicProfileDto = client.get("users/$id").body()

    /** 그 사람의 발행된 글, 최신순. */
    suspend fun userPosts(id: String, offset: Int = 0, limit: Int = 20): FeedDto =
        client.get("users/$id/posts") {
            parameter("offset", offset)
            parameter("limit", limit)
        }.body()

    /** [following] false = 그 사람을 팔로우하는 사람들, true = 그 사람이 팔로우하는 사람들. 최근 팔로우 순. */
    suspend fun userRelations(id: String, following: Boolean, offset: Int = 0, limit: Int = 30): UserListDto =
        client.get("users/$id/" + if (following) "following" else "followers") {
            parameter("offset", offset)
            parameter("limit", limit)
        }.body()

    /** 차단(POST)/해제(DELETE). 둘 다 멱등. 차단하면 서버가 양방향 팔로우도 끊는다. */
    suspend fun setBlock(id: String, block: Boolean) {
        val path = "users/$id/block"
        if (block) client.post(path) else client.delete(path)
    }

    /** 내가 차단한 사람들, 최근 차단 순. */
    suspend fun blockedUsers(): BlockedListDto = client.get("blocks").body()

    /** 팔로우(POST)/언팔로우(DELETE). 둘 다 멱등. 서버 기준 팔로우 여부·팔로워 수를 돌려준다. */
    suspend fun setFollow(id: String, follow: Boolean): FollowDto {
        val path = "users/$id/follow"
        return (if (follow) client.post(path) else client.delete(path)).body()
    }

    /** 최신순. [cursor] = 지난 응답의 nextCursor. */
    suspend fun notifications(cursor: String? = null): NotificationListDto =
        client.get("notifications") { cursor?.let { parameter("cursor", it) } }.body()

    suspend fun unreadNotificationCount(): UnreadCountDto = client.get("notifications/unread-count").body()

    /** [upTo] = 받은 가장 최신 알림의 createdAt — 그 뒤에 온 알림은 안 읽음으로 남는다. */
    suspend fun markNotificationsRead(upTo: String) {
        client.post("notifications/read") { parameter("upTo", upTo) }
    }

    // ---------- 회원가입 ----------
    suspend fun signupState(): SignupStateDto = client.get("me/signup").body()

    suspend fun agreeTerms(req: TermsDto) {
        client.post("me/signup/terms") { setBody(req) }
    }

    suspend fun requestIdentity(req: IdentityStartDto): IdentityStartResultDto =
        client.post("me/identity/request") { setBody(req) }.body()

    suspend fun verifyIdentity(req: IdentityVerifyDto): IdentityVerifiedDto =
        client.post("me/identity/verify") { setBody(req) }.body()

    suspend fun checkHandle(handle: String): HandleCheckDto =
        client.get("handles/check") { parameter("handle", handle) }.body()

    suspend fun completeSignup(req: CompleteSignupDto) {
        client.post("me/signup/complete") { setBody(req) }
    }

    /** 가입을 그만둠 — 가입을 마치지 않은 이 계정을 지운다 */
    /** 네이버 access token → 우리 계정 세션 (서버가 네이버에 확인). 틀린 토큰이면 401 */
    suspend fun naverLogin(accessToken: String): com.gentlelady.reborn.core.auth.SessionDto =
        client.post("auth/naver") {
            contentType(ContentType.Application.Json)
            setBody(NaverLoginDto(accessToken))
        }.body()

    suspend fun abandonSignup() {
        client.delete("me/signup")
    }

    /** 좋아요 켜기(POST)/끄기(DELETE). 둘 다 멱등 — 같은 요청을 여러 번 보내도 결과는 같다. */
    suspend fun setLike(target: InteractionTarget, id: String, liked: Boolean): LikeDto {
        val path = "${target.parentPath}/$id/like"
        return (if (liked) client.post(path) else client.delete(path)).body()
    }

    /** 오래된 순. [offset] = 지난 응답의 nextOffset. */
    suspend fun comments(target: InteractionTarget, id: String, offset: Int = 0): CommentListDto =
        client.get("${target.parentPath}/$id/comments") { parameter("offset", offset) }.body()

    suspend fun addComment(target: InteractionTarget, id: String, body: String): CommentDto =
        client.post("${target.parentPath}/$id/comments") { setBody(CreateCommentDto(body)) }.body()

    suspend fun deleteComment(target: InteractionTarget, commentId: String) {
        client.delete("${target.commentPath}/$commentId")
    }
}

/** 좋아요·댓글이 달리는 곳. 서버 경로가 다르다. */
enum class InteractionTarget(val parentPath: String, val commentPath: String) {
    POST("posts", "post-comments"),
    HISTORY("history", "history-comments"),
}

private const val UPLOAD_TIMEOUT_MS = 60_000L
const val GUESTBOOK_PAGE_SIZE = 30

@kotlinx.serialization.Serializable
data class NaverLoginDto(val accessToken: String)
