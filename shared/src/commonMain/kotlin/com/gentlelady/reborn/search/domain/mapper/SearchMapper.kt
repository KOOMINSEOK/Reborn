package com.gentlelady.reborn.search.domain.mapper

import com.gentlelady.reborn.search.presentation.SearchItem
import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.memorial_swipe.domain.model.MemorialItem
import com.gentlelady.reborn.memorial_swipe.domain.model.MusicItem
import reborn.shared.generated.resources.Res
import reborn.shared.generated.resources.img_memorial_bg_dummy
import reborn.shared.generated.resources.img_memorial_profile_dummy

// SearchItem을 HomePost로 변환하는 확장 함수 정의
fun SearchItem.toHomePost(): HomePost {
    return HomePost(
        id = this.id,
        authorName = this.title, // 시안에 맞춰 title을 authorName으로 매핑
        authorProfileUrl = "",    // 필요시 매핑 로직 추가
        contentImageUrl = "",     // 필요시 매핑 로직 추가
        caption = this.subtitle,  // subtitle을 caption으로 매핑
        postedAt = "",
        isPosthumous = false      // 타입에 따라 분기 처리 가능
    )
}

fun SearchItem.toMemorialItem(): MemorialItem {
    return MemorialItem(
        id = this.id,
        name = this.title,
        jobTitle = "고인", // SearchItem의 필드에서 적절히 매핑
        location = "서울", // SearchItem의 필드에서 적절히 매핑
        birthDate = "1990.01.01", // 필요 시 매핑 로직 추가
        deathDate = "2024.01.01",
        profileImageUrl = Res.drawable.img_memorial_profile_dummy, // 더미 이미지 매핑
        backgroundImageUrl = Res.drawable.img_memorial_bg_dummy,
        currentMusic = MusicItem("제목", "아티스트", "")
    )
}