package com.gentlelady.reborn.data

import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.memorial_swipe.domain.model.MemorialItem
import com.gentlelady.reborn.memorial_swipe.domain.model.MusicItem
import reborn.shared.generated.resources.Res
import reborn.shared.generated.resources.img_memorial_bg_dummy
import reborn.shared.generated.resources.img_memorial_profile_dummy

object MockDataSource {
    val homePosts = listOf(
        HomePost(
            id = "1",
            authorName = "홍길동",
            caption = "설날을 맞아 북한산으로 다녀왔습니다.",
            isPosthumous = false,
            authorProfileUrl = "", // 실제 리소스 경로 또는 URL 입력
            contentImageUrl = "",
            isLocked = false,
            likes = 12,
            comments = 3,
            postedAt = "2026.02.18"
        ),
        HomePost(
            id = "2",
            authorName = "김첨지",
            caption = "나의 마지막 기록이 여러분에게 닿기를...",
            isPosthumous = true,
            authorProfileUrl = "",
            contentImageUrl = "",
            isLocked = true,
            likes = 150,
            comments = 45,
            postedAt = "2026.01.03"
        )
    )

    val memorialItems = listOf(
        MemorialItem(
            id = "1",
            name = "김첨지",
            jobTitle = "소방관",
            location = "서울특별시",
            birthDate = "1987.03.02",
            deathDate = "2024.01.03",
            profileImageUrl = Res.drawable.img_memorial_profile_dummy,
            backgroundImageUrl = Res.drawable.img_memorial_bg_dummy,
            currentMusic = MusicItem("See You Again", "Wiz Khalifa ft. Charlie Puth", "")
        ),
        MemorialItem(
            id = "2",
            name = "홍길순",
            jobTitle = "간호사",
            location = "부산광역시",
            birthDate = "1992.05.15",
            deathDate = "2023.11.20",
            profileImageUrl = Res.drawable.img_memorial_profile_dummy,
            backgroundImageUrl = Res.drawable.img_memorial_bg_dummy,
            currentMusic = MusicItem("천 개의 바람이 되어", "임형주", "")
        )
    )
}