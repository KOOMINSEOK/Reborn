package com.gentlelady.reborn.feature.feedwrite

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
