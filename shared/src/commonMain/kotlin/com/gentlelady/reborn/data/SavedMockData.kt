package com.gentlelady.reborn.data

import com.gentlelady.reborn.management.saved.domain.model.SavedGridItem
import com.gentlelady.reborn.management.saved.domain.model.SavedGuestBookItem
import reborn.shared.generated.resources.Res
import reborn.shared.generated.resources.img_memorial_album_dummy
import reborn.shared.generated.resources.img_memorial_bg_dummy
import reborn.shared.generated.resources.img_post_dummy1
import reborn.shared.generated.resources.img_post_dummy2
import reborn.shared.generated.resources.img_post_dummy3
import reborn.shared.generated.resources.img_profile_dummy_1
import reborn.shared.generated.resources.img_profile_dummy_2
import reborn.shared.generated.resources.img_profile_dummy_3
import reborn.shared.generated.resources.img_profile_dummy_4
import reborn.shared.generated.resources.img_profile_dummy_5

object SavedMockData {

    private val thumbnailPool = listOf(
        Res.drawable.img_post_dummy1,
        Res.drawable.img_post_dummy2,
        Res.drawable.img_post_dummy3,
        Res.drawable.img_memorial_bg_dummy,
        Res.drawable.img_memorial_album_dummy,
        Res.drawable.img_profile_dummy_1,
        Res.drawable.img_profile_dummy_2,
        Res.drawable.img_profile_dummy_3,
        Res.drawable.img_profile_dummy_4,
        Res.drawable.img_profile_dummy_5
    )

    val savedPosts = (1..12).map { index ->
        SavedGridItem(id = "post_$index", thumbnail = thumbnailPool[(index - 1) % thumbnailPool.size])
    }

    val savedHistory = (1..6).map { index ->
        SavedGridItem(id = "history_$index", thumbnail = thumbnailPool[(index - 1) % thumbnailPool.size])
    }

    val savedGuestBookEntries = listOf(
        SavedGuestBookItem(
            id = "1",
            memorialName = "故 김영희 님의 메모리얼",
            message = "하늘에서도 평안하시길 바랍니다. 늘 기억하겠습니다.",
            dateLabel = "2026.07.27 18:27",
            authorAvatar = Res.drawable.img_profile_dummy_2
        ),
        SavedGuestBookItem(
            id = "2",
            memorialName = "故 이철수 님의 메모리얼",
            message = "따뜻했던 미소가 아직도 선명하네요. 보고 싶습니다.",
            dateLabel = "2025.10.12 14:05",
            authorAvatar = Res.drawable.img_profile_dummy_1
        )
    )
}
