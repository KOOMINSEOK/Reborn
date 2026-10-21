package com.gentlelady.reborn.notification

import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.NotificationDto
import com.gentlelady.reborn.core.network.dto.toAbsoluteServerUrl
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

enum class NotificationType { POST_LIKE, POST_COMMENT, HISTORY_LIKE, HISTORY_COMMENT, FOLLOW, GUESTBOOK }

data class Notification(
    val id: String,
    val type: NotificationType,
    val actorId: String,
    val actorName: String,
    val actorAvatarUrl: String?,
    val postId: String?,
    val memorialId: String?,
    val memorialName: String?,
    /** 댓글·방명록 id */
    val sourceId: String?,
    val preview: String?,
    /** 그 글의 첫 사진 */
    val imageUrl: String?,
    /** 서버 시각 그대로 (읽음 처리 기준으로 다시 보낸다) */
    val createdAt: String,
    val read: Boolean,
) {
    /** 이름 뒤에 붙는 말. 화면은 이름을 굵게 + 이 문장. */
    val message: String
        get() = when (type) {
            NotificationType.POST_LIKE -> "님이 회원님의 게시글을 좋아해요."
            NotificationType.POST_COMMENT -> "님이 회원님의 게시글에 댓글을 남겼어요: ${preview.orEmpty()}"
            NotificationType.HISTORY_LIKE -> "님이 회원님의 추억 글을 좋아해요."
            NotificationType.HISTORY_COMMENT -> "님이 회원님의 추억 글에 댓글을 남겼어요: ${preview.orEmpty()}"
            NotificationType.FOLLOW -> "님이 회원님을 팔로우하기 시작했어요."
            NotificationType.GUESTBOOK -> "님이 ${memorialName.orEmpty()}님의 추모 페이지에 방명록을 남겼어요: ${preview.orEmpty()}"
        }
}

/** "방금" · "5분 전" · "3시간 전" · "2일 전" · 일주일 넘으면 "10월 3일" */
fun relativeTime(createdAt: String, now: Instant = Clock.System.now(), zone: TimeZone = TimeZone.currentSystemDefault()): String {
    val at = runCatching { Instant.parse(createdAt) }.getOrNull() ?: return ""
    val ago: Duration = now - at
    return when {
        ago.inWholeMinutes < 1 -> "방금"
        ago.inWholeHours < 1 -> "${ago.inWholeMinutes}분 전"
        ago.inWholeDays < 1 -> "${ago.inWholeHours}시간 전"
        ago.inWholeDays < 7 -> "${ago.inWholeDays}일 전"
        else -> at.toLocalDateTime(zone).let { "${it.monthNumber}월 ${it.dayOfMonth}일" }
    }
}

/** 알림. mock 폴백 없음 — 실패 시 예외 (화면이 오류를 보여준다). */
interface NotificationRepository {
    /** 최신순 한 페이지 + 다음 커서 (null 이면 끝). 모르는 종류는 건너뛴다 (서버가 먼저 늘어나도 앱이 죽지 않게). */
    suspend fun notifications(cursor: String?): Pair<List<Notification>, String?>
    suspend fun unreadCount(): Int
    /** [upTo] (받은 가장 최신 알림의 createdAt) 까지 읽음으로 */
    suspend fun markRead(upTo: String)
}

class NotificationRepositoryImpl(private val api: RebornApi) : NotificationRepository {

    override suspend fun notifications(cursor: String?): Pair<List<Notification>, String?> =
        api.notifications(cursor).let { page -> page.items.mapNotNull { it.toNotification() } to page.nextCursor }

    override suspend fun unreadCount(): Int = api.unreadNotificationCount().count

    override suspend fun markRead(upTo: String) = api.markNotificationsRead(upTo)

    private fun NotificationDto.toNotification(): Notification? {
        val type = NotificationType.entries.firstOrNull { it.name.equals(type, ignoreCase = true) } ?: return null
        return Notification(
            id = id,
            type = type,
            actorId = actorId,
            actorName = actorName,
            actorAvatarUrl = actorAvatarUrl.toAbsoluteServerUrl(),
            postId = postId,
            memorialId = memorialId,
            memorialName = memorialName,
            sourceId = sourceId,
            preview = preview,
            imageUrl = imageUrl.toAbsoluteServerUrl(),
            createdAt = createdAt,
            read = read,
        )
    }
}

class GetNotificationsUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(cursor: String?) = repository.notifications(cursor)
}

class GetUnreadNotificationCountUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke() = repository.unreadCount()
}

class MarkNotificationsReadUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(upTo: String) = repository.markRead(upTo)
}
