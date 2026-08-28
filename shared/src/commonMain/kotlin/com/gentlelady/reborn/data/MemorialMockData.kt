package com.gentlelady.reborn.data

import reborn.shared.generated.resources.Res

import com.gentlelady.reborn.memorial.presentation.MemorialGuestBookItem
import com.gentlelady.reborn.memorial.presentation.MemorialHistoryItem
import com.gentlelady.reborn.memorial.presentation.MemorialProfileData
import com.gentlelady.reborn.memorial.presentation.MemorialState
import com.gentlelady.reborn.memorial.presentation.MemorialTab
import com.gentlelady.reborn.memorial.presentation.MemorialWreathItem
import org.jetbrains.compose.resources.DrawableResource
import reborn.shared.generated.resources.img_memorial_profile_dummy
import reborn.shared.generated.resources.img_post_grid_1
import reborn.shared.generated.resources.img_post_grid_2
import reborn.shared.generated.resources.img_post_grid_3
import reborn.shared.generated.resources.img_post_grid_4
import reborn.shared.generated.resources.img_post_grid_5
import reborn.shared.generated.resources.img_post_grid_6
import reborn.shared.generated.resources.img_post_grid_7
import reborn.shared.generated.resources.img_post_grid_8
import reborn.shared.generated.resources.img_post_grid_9
import reborn.shared.generated.resources.img_profile_dummy_1

object MemorialMockData {
    val dummyProfileRes = Res.drawable.img_profile_dummy_1

    // 1. 히스토리(추억) 그리드/상세 공용 데이터 (img_post_grid_1 ~ 9)
    val historyItems: List<MemorialHistoryItem> = listOf(
        MemorialHistoryItem(
            id = "h1",
            imageRes = Res.drawable.img_post_grid_1,
            authorName = "김영희",
            date = "2026년 7월 22일",
            caption = "첨자야~ 이때 기억나? 너랑 나랑 진짜 맛있는 레스토랑 가서 밥 먹고 영화봤던 날! 아직도 이때가 새록새록한데~~ 그립다^^",
            likes = 124,
            comments = 15
        ),
        MemorialHistoryItem(
            id = "h2",
            imageRes = Res.drawable.img_post_grid_2,
            authorName = "박지연",
            date = "2026년 5월 3일",
            caption = "오랜만에 시외로 나왔는데 정말 힐링이 됩니다. 이 풍경을 보니 걱정이 다 사라지는 것 같네요.",
            likes = 98,
            comments = 9
        ),
        MemorialHistoryItem(
            id = "h3",
            imageRes = Res.drawable.img_post_grid_3,
            authorName = "이현우",
            date = "2026년 4월 12일",
            caption = "그때 같이 갔던 호숫가, 아직도 눈에 선하다.",
            likes = 76,
            comments = 6
        ),
        MemorialHistoryItem(
            id = "h4",
            imageRes = Res.drawable.img_post_grid_4,
            authorName = "최민수",
            date = "2026년 3월 8일",
            caption = "숲길을 걸으며 나눴던 대화들, 평화로운 시간이었어.",
            likes = 64,
            comments = 4
        ),
        MemorialHistoryItem(
            id = "h5",
            imageRes = Res.drawable.img_post_grid_5,
            authorName = "정수아",
            date = "2026년 2월 20일",
            caption = "산 정상에서 함께 본 노을, 잊지 못할 거야.",
            likes = 152,
            comments = 21
        ),
        MemorialHistoryItem(
            id = "h6",
            imageRes = Res.drawable.img_post_grid_6,
            authorName = "김영희",
            date = "2026년 1월 15일",
            caption = "겨울 산책, 춥지만 좋았던 하루.",
            likes = 45,
            comments = 3
        ),
        MemorialHistoryItem(
            id = "h7",
            imageRes = Res.drawable.img_post_grid_7,
            authorName = "박지연",
            date = "2025년 12월 2일",
            caption = "그 카페에서 마신 커피 맛이 아직도 기억나.",
            likes = 87,
            comments = 8
        ),
        MemorialHistoryItem(
            id = "h8",
            imageRes = Res.drawable.img_post_grid_8,
            authorName = "이현우",
            date = "2025년 10월 19일",
            caption = "바닷가에서 보낸 여유로운 오후.",
            likes = 110,
            comments = 12
        ),
        MemorialHistoryItem(
            id = "h9",
            imageRes = Res.drawable.img_post_grid_9,
            authorName = "최민수",
            date = "2025년 9월 5일",
            caption = "해변을 걸으며 나눴던 이야기들이 그립다.",
            likes = 132,
            comments = 17
        ),
        MemorialHistoryItem(
            id = "h10",
            imageRes = Res.drawable.img_post_grid_1,
            authorName = "정수아",
            date = "2025년 7월 30일",
            caption = "그때 다 같이 먹었던 저녁, 웃음이 끊이질 않았지.",
            likes = 58,
            comments = 5
        ),
        MemorialHistoryItem(
            id = "h11",
            imageRes = Res.drawable.img_post_grid_2,
            authorName = "김영희",
            date = "2025년 6월 14일",
            caption = "여름 초입, 시원한 바람이 좋았던 날.",
            likes = 71,
            comments = 6
        ),
        MemorialHistoryItem(
            id = "h12",
            imageRes = Res.drawable.img_post_grid_3,
            authorName = "박지연",
            date = "2025년 5월 2일",
            caption = "함께 걸었던 그 길, 아직도 눈에 선해.",
            likes = 93,
            comments = 10
        ),
        MemorialHistoryItem(
            id = "h13",
            imageRes = Res.drawable.img_post_grid_4,
            authorName = "이현우",
            date = "2025년 3월 21일",
            caption = "봄바람 맞으며 나눴던 이야기들.",
            likes = 66,
            comments = 7
        ),
        MemorialHistoryItem(
            id = "h14",
            imageRes = Res.drawable.img_post_grid_5,
            authorName = "최민수",
            date = "2025년 1월 9일",
            caption = "새해 첫날, 함께 본 첫 해돋이.",
            likes = 140,
            comments = 19
        )
    )

    // 1-1. 온라인 화환 앨범 샘플 데이터 (지금은 사진 대신 화환을 보낸 단체/사람 이름을 타일에 표시)
    val onlineWreathItems: List<MemorialWreathItem> = listOf(
        MemorialWreathItem("w1", "김철수"),
        MemorialWreathItem("w2", "홍길동"),
        MemorialWreathItem("w3", "이영희"),
        MemorialWreathItem("w4", "IBAS"),
        MemorialWreathItem("w5", "인하가족"),
        MemorialWreathItem("w6", "인천가족모임"),
        MemorialWreathItem("w7", "ITDA"),
        MemorialWreathItem("w8", "사랑하는이들"),
        MemorialWreathItem("w9", "동아리모임"),
        MemorialWreathItem("w10", "박민수"),
        MemorialWreathItem("w11", "정수아"),
        MemorialWreathItem("w12", "대학동기"),
        MemorialWreathItem("w13", "회사동료일동"),
        MemorialWreathItem("w14", "이웃사랑회")
    )

    // 2. 방명록 샘플 데이터 (시안 1 기반)
    val guestBookMessages = listOf(
        MemorialGuestBookItem("1", "지연", null, "오늘따라 너 생각이 나니 ㅋㅋ 잘 지내지? 길동아 어제 헤어졌다. 너랑 술 한잔 하고 싶네", "오늘, 10:30 AM"),
        MemorialGuestBookItem("2", "나", null, "보고싶어 ♥", "어제, 4:15 PM"),
        MemorialGuestBookItem("3", "현우", null, "오늘 김첨지랑 같이 우리 예전에 갔던 올림픽 공원 갔어. 생각나서 찍었던 사진도 봤는데 ㅋㅋㅋ 길동아 거긴 날씨가 어떠냐?", "10월 24일, 9:00 AM")
    )

    // 추모 페이지(고인: 홍길동) — 타인이 개설한 페이지를 보는 시점. 편집 개념 없음.
    val memorialState = MemorialState(
        profile = MemorialProfileData(
            id = "seed_memorial",
            name = "홍길동",
            handle = "seed_memorial_hong",
            bio = "인생, 헤맨만큼 내 땅이다",
            followerCount = 5,
            profileImageRes = dummyProfileRes
        ),
        selectedTab = MemorialTab.HISTORY,
        historyItems = historyItems,
        onlineWreathItems = onlineWreathItems,
        guestBookMessages = guestBookMessages,
    )
}