package com.gentlelady.reborn.data

import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.memorial_swipe.domain.model.MemorialItem
import com.gentlelady.reborn.memorial_swipe.domain.model.MusicItem
import com.gentlelady.reborn.search.domain.entity.MemorialSearchItem
import reborn.shared.generated.resources.Res
import reborn.shared.generated.resources.img_memorial_album_dummy
import reborn.shared.generated.resources.img_memorial_bg_dummy
import reborn.shared.generated.resources.img_memorial_profile_dummy
import reborn.shared.generated.resources.img_post_dummy1
import reborn.shared.generated.resources.img_post_dummy2
import reborn.shared.generated.resources.img_profile_dummy_1
import reborn.shared.generated.resources.img_profile_dummy_2
import reborn.shared.generated.resources.img_profile_dummy_3
import reborn.shared.generated.resources.img_profile_dummy_4
import reborn.shared.generated.resources.img_profile_dummy_5

object MockDataSource {

    val homePosts = listOf(
        HomePost(
            id = "1",
            authorName = "홍길동",
            caption = "설날을 맞아 북한산으로 다녀왔습니다.",
            isPosthumous = false,
            // flat하게 저장된 파일명 규칙(img_post_dummy1)에 맞춰 직접 매핑
            authorProfileUrl = Res.drawable.img_memorial_profile_dummy,
            contentImageUrl = Res.drawable.img_post_dummy1,
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
            authorProfileUrl = Res.drawable.img_profile_dummy_2,
            contentImageUrl = Res.drawable.img_post_dummy2,
            isLocked = true,
            likes = 150,
            comments = 45,
            postedAt = "2026.01.03"
        )
    )

    val memorialItems = listOf(
        MemorialItem(
            id = "1",
            rank = 1, // 기존 컴포넌트 구조(rank) 반영
            name = "김첨지",
            jobTitle = "소방관",
            location = "서울특별시",
            flowerCount = "24.8k",
            birthDate = "1987.03.02",
            deathDate = "2024.01.03",
            profileImageUrl = Res.drawable.img_memorial_profile_dummy,
            backgroundImageUrl = Res.drawable.img_memorial_album_dummy,
            currentMusic = MusicItem("See You Again", "Wiz Khalifa ft. Charlie Puth", "")
        ),
        MemorialItem(
            id = "2",
            rank = 2,
            name = "홍길순",
            jobTitle = "간호사",
            location = "부산광역시",
            flowerCount = "18.5k",
            birthDate = "1992.05.15",
            deathDate = "2023.11.20",
            profileImageUrl = Res.drawable.img_profile_dummy_2, // 필요시 별도 에셋 추가 지정
            backgroundImageUrl = Res.drawable.img_memorial_album_dummy,
            currentMusic = MusicItem("천 개의 바람이 되어", "임형주", "")
        ),
        // 검색(Search) 및 리스트 뷰 다채로움을 위한 일반 랭킹 데이터 추가
        MemorialItem(
            id = "3",
            rank = 4,
            name = "이순신",
            jobTitle = "장군",
            location = "충청남도",
            flowerCount = "1.2k",
            birthDate = "1545.04.28",
            deathDate = "1598.12.16",
            profileImageUrl = Res.drawable.img_memorial_profile_dummy,
            backgroundImageUrl = Res.drawable.img_memorial_album_dummy, // 다른 배경 에셋 활용
            currentMusic = null
        )

    )
    val memorialSearchItems = listOf(
        MemorialSearchItem(
            id = "mem_search_1",
            rank = 1,
            name = "이수진",
            birthDate = "1965",
            deathDate = "2023",
            location = "서울",
            flowerCount = "24.8k",
            profileImageUrl = Res.drawable.img_profile_dummy_1,
            isVerified = false
        ),
        MemorialSearchItem(
            id = "mem_search_2",
            rank = 2,
            name = "김민준",
            birthDate = "1978",
            deathDate = "2022",
            location = "부산",
            flowerCount = "18.3k",
            profileImageUrl = Res.drawable.img_profile_dummy_2,
            isVerified = true // 블루 체크 마크 활성화
        ),
        MemorialSearchItem(
            id = "mem_search_3",
            rank = 3,
            name = "박영희",
            birthDate = "1990",
            deathDate = "2024",
            location = "인천",
            flowerCount = "12.5k",
            profileImageUrl = Res.drawable.img_profile_dummy_3,
            isVerified = false
        ),
        MemorialSearchItem(
            id = "mem_search_4",
            rank = 4,
            name = "최동현",
            birthDate = "1955",
            deathDate = "2021",
            location = "대구",
            flowerCount = "9.7k",
            profileImageUrl = Res.drawable.img_profile_dummy_4,
            isVerified = false
        ),
        MemorialSearchItem(
            id = "mem_search_5",
            rank = 5,
            name = "정소연",
            birthDate = "1982",
            deathDate = "2023",
            location = "광주",
            flowerCount = "7.1k",
            profileImageUrl = Res.drawable.img_profile_dummy_5,
            isVerified = false
        )
    )
}