package com.gentlelady.reborn.data

import reborn.shared.generated.resources.Res

import com.gentlelady.reborn.memorial.presentation.MemorialGuestBookItem
import com.gentlelady.reborn.memorial.presentation.MemorialOwnerType
import com.gentlelady.reborn.memorial.presentation.MemorialProfileData
import com.gentlelady.reborn.memorial.presentation.MemorialState
import com.gentlelady.reborn.memorial.presentation.MemorialTab
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
    // 1. img_post_grid_1 ~ 9 까지의 히스토리 에셋 리스트 (1:1 정사각형 그리드용)
    val historyImages: List<DrawableResource> = listOf(
        Res.drawable.img_post_grid_1,
        Res.drawable.img_post_grid_2,
        Res.drawable.img_post_grid_3,
        Res.drawable.img_post_grid_4,
        Res.drawable.img_post_grid_5,
        Res.drawable.img_post_grid_6,
        Res.drawable.img_post_grid_7,
        Res.drawable.img_post_grid_8,
        Res.drawable.img_post_grid_9
    )

    // 2. 방명록 샘플 데이터 (시안 1 기반)
    val guestBookMessages = listOf(
        MemorialGuestBookItem("1", "지연", null, "오늘따라 너 생각이 나니 ㅋㅋ 잘 지내지? 길동아 어제 헤어졌다. 너랑 술 한잔 하고 싶네", "오늘, 10:30 AM"),
        MemorialGuestBookItem("2", "민수", null, "보고싶어 ♥", "어제, 4:15 PM"),
        MemorialGuestBookItem("3", "현우", null, "오늘 김첨지랑 같이 우리 예전에 갔던 올림픽 공원 갔어. 생각나서 찍었던 사진도 봤는데 ㅋㅋㅋ 길동아 거긴 날씨가 어떠냐?", "10월 24일, 9:00 AM")
    )

    // 3. 타인 시점 MemorialState (홍길동 페이지)
    val otherMemorialState = MemorialState(
        ownerType = MemorialOwnerType.OTHER_MEMORIAL,
        profile = MemorialProfileData(
            id = "other_1",
            name = "홍길동",
            handle = "uexjurjece",
            bio = "인생, 헤맨만큼 내 땅이다",
            followerCount = 5
        ),
        selectedTab = MemorialTab.HISTORY,
        historyImages = historyImages,
        guestBookMessages = guestBookMessages
    )

    // 4. 내 시점 MemorialState (이윤주 페이지)
    val myMemorialState = MemorialState(
        ownerType = MemorialOwnerType.MY_MEMORIAL,
        profile = MemorialProfileData(
            id = "my_1",
            name = "이윤주",
            handle = "uexjurjece",
            bio = "Forever in our hearts, guiding us with love and light.",
            followerCount = 12,
            profileImageRes = dummyProfileRes
        ),
        selectedTab = MemorialTab.HISTORY,
        historyImages = historyImages,
        guestBookMessages = guestBookMessages
    )
}