package com.gentlelady.reborn.server.seed

import java.util.UUID

/** 앱의 HomeMockData / MemorialMockData 를 서버 시드로 옮긴 것. 고정 UUID → idempotent. */
internal object SeedData {

    data class SeedUser(val id: UUID, val name: String, val email: String)
    data class SeedPost(val id: UUID, val authorName: String, val caption: String, val image: String, val posthumous: Boolean, val daysAgo: Long)
    data class SeedHistory(val id: UUID, val authorName: String, val caption: String, val image: String, val daysAgo: Long)
    data class SeedGuestbook(val id: UUID, val authorName: String, val message: String, val daysAgo: Long)

    private fun uid(suffix: String) = UUID.fromString("00000000-0000-0000-0000-0000000000$suffix")

    val users = listOf(
        SeedUser(uid("a1"), "홍길동", "seed-hong@reborn.local"),
        SeedUser(uid("a2"), "김첨지", "seed-kimc@reborn.local"),
        SeedUser(uid("a3"), "김영희", "seed-younghee@reborn.local"),
        SeedUser(uid("a4"), "박지연", "seed-jiyeon@reborn.local"),
        SeedUser(uid("a5"), "이현우", "seed-hyunwoo@reborn.local"),
        SeedUser(uid("a6"), "최민수", "seed-minsu@reborn.local"),
        SeedUser(uid("a7"), "정수아", "seed-sua@reborn.local"),
    )

    val userIdByName = users.associate { it.name to it.id }

    val posts = listOf(
        SeedPost(uid("b1"), "홍길동", "설날을 맞아 북한산으로 다녀왔습니다.", "img_post_dummy1.png", posthumous = false, daysAgo = 5),
        SeedPost(uid("b2"), "김첨지", "나의 마지막 기록이 여러분에게 닿기를...", "img_post_dummy2.png", posthumous = true, daysAgo = 40),
    )

    /** 예약발행 데모용. publish_at 이 seed 후 2분 뒤 → 티커/스케줄러가 published 로 뒤집는다. */
    data class SeedScheduledPost(val id: UUID, val authorName: String, val caption: String, val publishInMinutes: Long)
    val scheduledPosts = listOf(
        SeedScheduledPost(uid("b3"), "정수아", "예약 발행 테스트 글입니다. 발행 시각이 지나면 피드에 나타납니다.", publishInMinutes = 2),
    )

    /** 홍길동(고인) 추모 페이지. 개설자는 타인(김영희). */
    val memorialId = uid("c1")
    const val MEMORIAL_NAME = "홍길동"
    const val MEMORIAL_HANDLE = "seed_memorial_hong"
    const val MEMORIAL_BIO = "인생, 헤맨만큼 내 땅이다"
    const val MEMORIAL_CREATOR = "김영희"

    val history = listOf(
        SeedHistory(uid("d1"), "김영희", "첨지야~ 이때 기억나? 너랑 나랑 맛있는 레스토랑 가서 밥 먹고 영화봤던 날! 그립다^^", "img_post_grid_1.png", 38),
        SeedHistory(uid("d2"), "박지연", "오랜만에 시외로 나왔는데 정말 힐링이 됩니다.", "img_post_grid_2.png", 117),
        SeedHistory(uid("d3"), "이현우", "그때 같이 갔던 호숫가, 아직도 눈에 선하다.", "img_post_grid_3.png", 138),
        SeedHistory(uid("d4"), "최민수", "숲길을 걸으며 나눴던 대화들, 평화로운 시간이었어.", "img_post_grid_4.png", 173),
        SeedHistory(uid("d5"), "정수아", "산 정상에서 함께 본 노을, 잊지 못할 거야.", "img_post_grid_5.png", 189),
        SeedHistory(uid("d6"), "김영희", "겨울 산책, 춥지만 좋았던 하루.", "img_post_grid_6.png", 225),
        SeedHistory(uid("d7"), "박지연", "그 카페에서 마신 커피 맛이 아직도 기억나.", "img_post_grid_7.png", 269),
        SeedHistory(uid("d8"), "이현우", "바닷가에서 보낸 여유로운 오후.", "img_post_grid_8.png", 313),
        SeedHistory(uid("d9"), "최민수", "해변을 걸으며 나눴던 이야기들이 그립다.", "img_post_grid_9.png", 358),
    )

    val guestbook = listOf(
        SeedGuestbook(uid("e1"), "박지연", "오늘따라 너 생각이 나니 ㅋㅋ 잘 지내지? 길동아 너랑 술 한잔 하고 싶네", 0),
        SeedGuestbook(uid("e2"), "정수아", "보고싶어 ♥", 1),
        SeedGuestbook(uid("e3"), "이현우", "오늘 예전에 갔던 올림픽 공원 갔어. 생각나서 찍었던 사진도 봤는데 ㅋㅋㅋ 길동아 거긴 날씨가 어떠냐?", 3),
    )
}
