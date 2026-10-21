package com.gentlelady.reborn.server.seed

import java.util.UUID

/** 앱의 HomeMockData / MemorialMockData 를 서버 시드로 옮긴 것 + 화면을 채울 예시 데이터. 고정 UUID → idempotent. */
internal object SeedData {

    /** 시드 내용을 바꾸면 올린다 → 다음 기동에 한 번 다시 돈다 (seed_runs). */
    const val VERSION = "2026-10-21.1"

    data class SeedUser(val id: UUID, val name: String, val email: String, val bio: String? = null, val avatar: String? = null)
    data class SeedPost(
        val id: UUID,
        val authorName: String,
        val caption: String,
        val images: List<String>,
        val posthumous: Boolean,
        val daysAgo: Long,
    )
    data class SeedHistory(val id: UUID, val authorName: String, val caption: String, val image: String, val daysAgo: Long)
    data class SeedGuestbook(val id: UUID, val authorName: String, val message: String, val daysAgo: Long)
    data class SeedComment(val id: UUID, val parentId: UUID, val authorName: String, val body: String)

    private fun uid(suffix: String) = UUID.fromString("00000000-0000-0000-0000-0000000000$suffix")

    /** 생성형 시드 id. [kind] 가 상위 비트라 위 uid("xx") 들과 겹치지 않는다. */
    private fun gen(kind: Int, n: Int) = UUID(0L, (kind.toLong() shl 40) or n.toLong())

    /** 프로필 사진은 사람 사진이 있는 두 명만 (나머지는 이름 첫 글자). 홍길동 = 추모 페이지 주인공. */
    val users = listOf(
        SeedUser(uid("a1"), "홍길동", "seed-hong@reborn.local", "인생, 헤맨만큼 내 땅이다", "img_post_grid_5.png"),
        SeedUser(uid("a2"), "김첨지", "seed-kimc@reborn.local", "오늘도 한 걸음"),
        SeedUser(uid("a3"), "김영희", "seed-younghee@reborn.local", "기록하는 사람"),
        SeedUser(uid("a4"), "박지연", "seed-jiyeon@reborn.local", "바다 좋아해요 🌊", "img_profile_dummy_1.png"),
        SeedUser(uid("a5"), "이현우", "seed-hyunwoo@reborn.local", "사진 찍는 개발자"),
        SeedUser(uid("a6"), "최민수", "seed-minsu@reborn.local"),
        SeedUser(uid("a7"), "정수아", "seed-sua@reborn.local", "느리게, 천천히"),
    )

    val userIdByName = users.associate { it.name to it.id }
    private val names = users.map { it.name }

    // 이미지 내용 — dummy1 산·운해, dummy2 나뭇잎, grid_1 밤거리, grid_2 해변 노을 단체, grid_3 마들렌,
    // grid_4 전깃줄 하늘, grid_5 청년 얼굴(홍길동), grid_6 바닷가 노을, grid_7 편의점 진열대, grid_8 카페 케이크,
    // grid_9 발표 현장, profile_dummy_1 여성 얼굴(박지연). 캡션은 사진과 맞춘다.
    val posts = listOf(
        SeedPost(uid("b1"), "홍길동", "설날을 맞아 북한산으로 다녀왔습니다.", listOf("img_post_dummy1.png"), posthumous = false, daysAgo = 5),
        SeedPost(uid("b2"), "김첨지", "나의 마지막 기록이 여러분에게 닿기를...", listOf("img_post_dummy2.png"), posthumous = true, daysAgo = 40),
        SeedPost(gen(1, 1), "박지연", "주말에 다녀온 바다. 노을 지는 것까지 보고 왔어요.", listOf("img_post_grid_6.png", "img_post_grid_2.png"), false, 0),
        SeedPost(gen(1, 2), "이현우", "퇴근길 하늘이 너무 예뻐서 한 장.", listOf("img_post_grid_4.png"), false, 1),
        SeedPost(gen(1, 3), "정수아", "친구들이랑 1박 2일. 산도 숲도 바다도 다 보고 왔다!", listOf("img_post_dummy1.png", "img_post_dummy2.png", "img_post_grid_2.png"), false, 1),
        SeedPost(gen(1, 4), "최민수", "동네 빵집 마들렌이 이렇게 맛있을 일인가.", listOf("img_post_grid_3.png"), false, 2),
        SeedPost(gen(1, 5), "김영희", "엄마랑 처음으로 단둘이 여행. 하늘이 정말 예뻤어요.", listOf("img_post_grid_4.png", "img_post_grid_6.png"), false, 3),
        SeedPost(gen(1, 6), "홍길동", "매일 아침 걷는 산책로. 오늘은 햇살이 유난히 좋았어요.", listOf("img_post_dummy2.png"), false, 4),
        SeedPost(gen(1, 7), "김첨지", "가족들에게. 내가 없더라도 웃는 날이 더 많았으면 좋겠다. 고마웠어.", listOf("img_post_grid_2.png"), true, 6),
        SeedPost(gen(1, 8), "박지연", "편의점 신상 구경하러 왔다가 장바구니가 터질 뻔.", listOf("img_post_grid_7.png"), false, 8),
        SeedPost(gen(1, 9), "이현우", "사이드 프로젝트 첫 발표! 떨렸지만 무사히 끝났다 🎉", listOf("img_post_grid_9.png"), false, 10),
        SeedPost(gen(1, 10), "정수아", "커피 한 잔, 케이크 한 조각. 이런 오후가 제일 좋아.", listOf("img_post_grid_8.png"), false, 12),
        SeedPost(gen(1, 11), "최민수", "10년 만에 다시 찾은 고향. 밤거리는 그대로라서 다행이다.", listOf("img_post_grid_1.png"), false, 15),
        SeedPost(gen(1, 12), "김영희", "아빠가 좋아하던 산에 다녀왔어요. 구름이 발밑에 깔려 있었어요.", listOf("img_post_dummy1.png"), false, 20),
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
    const val MEMORIAL_IMAGE = "img_post_grid_5.png"

    val history = listOf(
        SeedHistory(uid("d1"), "김영희", "길동아~ 이때 기억나? 같이 밥 먹고 영화 보고 밤거리 걸었던 날! 그립다^^", "img_post_grid_1.png", 38),
        SeedHistory(uid("d2"), "박지연", "다 같이 갔던 바다. 이 사진 볼 때마다 웃음이 나.", "img_post_grid_2.png", 117),
        SeedHistory(uid("d3"), "이현우", "네가 좋아하던 마들렌, 오늘 사왔어.", "img_post_grid_3.png", 138),
        SeedHistory(uid("d4"), "최민수", "그날 하늘도 딱 이랬지. 전깃줄 사이로 구름 보던 거.", "img_post_grid_4.png", 173),
        SeedHistory(uid("d5"), "정수아", "네 웃는 얼굴, 이 사진이 제일 좋아.", "img_post_grid_5.png", 189),
        SeedHistory(uid("d6"), "김영희", "바닷가 노을 보면서 네 얘기 많이 했어.", "img_post_grid_6.png", 225),
        SeedHistory(uid("d7"), "박지연", "편의점 앞에서 컵라면 먹던 밤, 기억나?", "img_post_grid_7.png", 269),
        SeedHistory(uid("d8"), "이현우", "그 카페에서 먹던 케이크, 아직도 팔더라.", "img_post_grid_8.png", 313),
        SeedHistory(uid("d9"), "최민수", "네가 발표하던 날. 정말 멋있었어.", "img_post_grid_9.png", 358),
    )

    private val guestbookMessages = listOf(
        "길동아 오늘 네가 좋아하던 국밥집 지나갔다. 아직 그대로더라.",
        "생일 축하해. 거기서도 케이크 먹고 있지?",
        "요즘 일이 많아서 정신없는데, 네가 있었으면 한마디 해줬을 텐데.",
        "꿈에 나와줘서 고마워. 웃고 있어서 다행이었어.",
        "오늘 비 온다. 너 비 오는 날 좋아했잖아.",
        "다들 잘 지내고 있어. 걱정하지 마.",
        "네가 추천해준 노래 아직도 듣고 있어.",
        "벌써 시간이 이렇게 됐네. 보고 싶다.",
        "주말에 너희 어머님 뵙고 왔어. 건강하셔.",
        "우리 갔던 그 바다 다시 가봤어. 너 몫까지 보고 왔다.",
    )

    /**
     * 방명록: 최근 3개 + 아래로 40개 → 한 페이지(30개)를 넘어서 무한 스크롤을 시험할 수 있다.
     * 작성자는 시드 유저를 돌아가며, 날짜는 하루~두 달 전으로 흩어 둔다.
     */
    val guestbook = listOf(
        SeedGuestbook(uid("e1"), "박지연", "오늘따라 너 생각이 나니 ㅋㅋ 잘 지내지? 길동아 너랑 술 한잔 하고 싶네", 0),
        SeedGuestbook(uid("e2"), "정수아", "보고싶어 ♥", 1),
        SeedGuestbook(uid("e3"), "이현우", "오늘 예전에 갔던 올림픽 공원 갔어. 생각나서 찍었던 사진도 봤는데 ㅋㅋㅋ 길동아 거긴 날씨가 어떠냐?", 3),
    ) + (1..40).map { n ->
        SeedGuestbook(
            gen(2, n),
            authorName = names[1 + n % (names.size - 1)], // 홍길동(고인) 본인은 빼고
            message = guestbookMessages[n % guestbookMessages.size],
            daysAgo = 3L + n * 3 / 2,
        )
    }

    /** 사람 팔로우: 모두가 홍길동·박지연을, 몇 명은 서로를 → 팔로잉 피드·팔로워 수가 비지 않게. */
    val follows: List<Pair<String, String>> = buildList {
        names.forEach { me ->
            listOf("홍길동", "박지연").filter { it != me }.forEach { add(me to it) }
        }
        add("박지연" to "이현우"); add("이현우" to "정수아"); add("정수아" to "김영희")
        add("김영희" to "최민수"); add("최민수" to "김첨지"); add("김첨지" to "정수아")
    }

    /** 홍길동 추모 페이지 팔로워 */
    val memorialFollowers = listOf("김영희", "박지연", "이현우", "정수아", "최민수", "김첨지")

    /** (글 인덱스 기준) 본인 제외 시드 유저 중 앞에서 몇 명이 좋아요. 글마다 수가 달라 보이게. */
    private fun likersFor(index: Int, author: String) =
        names.filter { it != author }.take((index * 5 + 3) % names.size)

    val postLikes: List<Pair<UUID, String>> =
        posts.flatMapIndexed { i, p -> likersFor(i, p.authorName).map { p.id to it } }

    val historyLikes: List<Pair<UUID, String>> =
        history.flatMapIndexed { i, h -> likersFor(i + 2, h.authorName).map { h.id to it } }

    private val commentLines = listOf(
        "와 사진 분위기 너무 좋다",
        "여기 어디야? 나도 가보고 싶어",
        "ㅋㅋㅋ 다음엔 나도 불러줘",
        "보기만 해도 힐링된다",
        "잘 지내는 것 같아서 좋네!",
        "이거 진짜 오랜만이다",
        "다음 주에 같이 가자",
        "늘 응원해 👍",
    )

    /** 글마다 0~4개. 댓글 무한 스크롤(30개 단위)용으로 첫 글엔 35개. */
    val postComments: List<SeedComment> = posts.flatMapIndexed { i, p ->
        val count = if (i == 2) 35 else i % 5
        (0 until count).map { k ->
            SeedComment(
                id = gen(3, i * 100 + k),
                parentId = p.id,
                authorName = names.filter { it != p.authorName }[(i + k) % (names.size - 1)],
                body = commentLines[(i + k) % commentLines.size],
            )
        }
    }

    private val historyCommentLines = listOf(
        "그날 정말 즐거웠지",
        "이 사진 처음 본다. 고마워",
        "길동이 웃는 모습이 눈에 선하다",
        "나도 그립다",
    )

    val historyComments: List<SeedComment> = history.flatMapIndexed { i, h ->
        (0 until (i % 4)).map { k ->
            SeedComment(
                id = gen(4, i * 100 + k),
                parentId = h.id,
                authorName = names.filter { it != h.authorName && it != "홍길동" }[(i + k) % (names.size - 2)],
                body = historyCommentLines[(i + k) % historyCommentLines.size],
            )
        }
    }
}
