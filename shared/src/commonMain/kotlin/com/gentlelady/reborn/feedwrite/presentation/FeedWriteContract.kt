package com.gentlelady.reborn.feedwrite.presentation

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus

enum class FeedPostType { LIVING, POSTHUMOUS }

enum class ScheduleOption(val label: String) {
    ONE_DAY("1일 후"),
    ONE_WEEK("1주 후"),
    THIRTY_DAYS("30일 후"),
    CUSTOM("사용자 지정")
}

enum class PostVisibility(val label: String) {
    PUBLIC("전체 공개"),
    FOLLOWERS("팔로워 공개"),
    PRIVATE("프라이빗 공개")
}

data class FollowerItem(
    val id: String,
    val name: String,
    val handle: String
)

// ponytail: 서버 팔로워 목록 API가 아직 없어 화면 시연용 고정 목록을 사용한다.
val MOCK_RECOMMENDED_FOLLOWERS = listOf(
    FollowerItem("1", "김철수", "@chulsoo_k"),
    FollowerItem("2", "이영희", "@younghee_l"),
    FollowerItem("3", "박지민", "@jimin_p"),
    FollowerItem("4", "최수아", "@sua_choi"),
    FollowerItem("5", "정우진", "@woojin_j")
)

/** 게시글 한 건에 붙일 수 있는 최대 사진 수. 서버 MAX_IMAGES 와 같게 유지. */
const val MAX_POST_IMAGES = 10

data class FeedWriteState(
    val caption: String = "",
    /** 첨부 사진(JPEG 바이트). 순서 = 게시글 표시 순서. */
    val images: List<ByteArray> = emptyList(),
    val scheduleOption: ScheduleOption = ScheduleOption.ONE_WEEK,
    val customDate: LocalDate? = null,
    // ponytail: 공개 범위·대상 팔로워는 서버 posts 에 아직 없음 → 화면에서만 고르고 전송하지 않는다.
    val visibility: PostVisibility = PostVisibility.FOLLOWERS,
    val selectedFollowerIds: Set<String> = emptySet(),
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    /** true 가 되면 화면이 작성 플로우를 닫는다. */
    val isSubmitted: Boolean = false,
)

/** 사후 게시글의 공개(발행) 시각. 생전 게시글은 즉시 발행이라 null. */
fun FeedWriteState.publishAt(type: FeedPostType, now: Instant, zone: TimeZone): Instant? {
    if (type == FeedPostType.LIVING) return null
    return when (scheduleOption) {
        ScheduleOption.ONE_DAY -> now.plus(1, DateTimeUnit.DAY, zone)
        ScheduleOption.ONE_WEEK -> now.plus(7, DateTimeUnit.DAY, zone)
        ScheduleOption.THIRTY_DAYS -> now.plus(30, DateTimeUnit.DAY, zone)
        ScheduleOption.CUSTOM -> customDate?.atStartOfDayIn(zone) ?: now.plus(7, DateTimeUnit.DAY, zone)
    }
}

sealed interface FeedWriteIntent {
    data class SelectScheduleOption(val option: ScheduleOption) : FeedWriteIntent
    data class SelectCustomDate(val date: LocalDate) : FeedWriteIntent
    data class SelectVisibility(val visibility: PostVisibility) : FeedWriteIntent
    data class ToggleFollower(val id: String) : FeedWriteIntent
    data class ChangeCaption(val caption: String) : FeedWriteIntent
    data class AddImage(val jpeg: ByteArray) : FeedWriteIntent
    data class RemoveImage(val index: Int) : FeedWriteIntent
    data class Submit(val type: FeedPostType) : FeedWriteIntent
}

sealed interface FeedWriteResult {
    data object Submitting : FeedWriteResult
    data object Submitted : FeedWriteResult
    data class SubmitFailed(val message: String) : FeedWriteResult
}
