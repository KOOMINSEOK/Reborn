package com.gentlelady.reborn.feedwrite.presentation

import kotlinx.datetime.LocalDate

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

data class FeedWriteState(
    val scheduleOption: ScheduleOption = ScheduleOption.ONE_WEEK,
    val customDate: LocalDate? = null,
    val visibility: PostVisibility = PostVisibility.FOLLOWERS,
    val selectedFollowerIds: Set<String> = emptySet()
)

sealed interface FeedWriteIntent {
    data class SelectScheduleOption(val option: ScheduleOption) : FeedWriteIntent
    data class SelectCustomDate(val date: LocalDate) : FeedWriteIntent
    data class SelectVisibility(val visibility: PostVisibility) : FeedWriteIntent
    data class ToggleFollower(val id: String) : FeedWriteIntent
}
