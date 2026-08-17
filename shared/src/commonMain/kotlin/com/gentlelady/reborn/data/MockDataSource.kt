package com.gentlelady.reborn.data

import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.memorial_swipe.domain.model.MemorialItem
import com.gentlelady.reborn.memorial_swipe.domain.model.MusicItem
import com.gentlelady.reborn.message.presentation.ChatRoomItem
import com.gentlelady.reborn.message.presentation.GuestBookItem
import com.gentlelady.reborn.search.domain.entity.MemorialSearchItem
import reborn.shared.generated.resources.Res
import reborn.shared.generated.resources.img_memorial_album_dummy
import reborn.shared.generated.resources.img_memorial_bg_dummy
import reborn.shared.generated.resources.img_memorial_profile_dummy
import reborn.shared.generated.resources.img_post_dummy1
import reborn.shared.generated.resources.img_post_dummy2
import reborn.shared.generated.resources.img_post_dummy3
import reborn.shared.generated.resources.img_profile_dummy_1
import reborn.shared.generated.resources.img_profile_dummy_2
import reborn.shared.generated.resources.img_profile_dummy_3
import reborn.shared.generated.resources.img_profile_dummy_4
import reborn.shared.generated.resources.img_profile_dummy_5

import com.gentlelady.reborn.profile.domain.model.ProfileFeedItem

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
            backgroundImageUrl = Res.drawable.img_memorial_bg_dummy,
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
            backgroundImageUrl = Res.drawable.img_memorial_bg_dummy,
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
            backgroundImageUrl = Res.drawable.img_memorial_bg_dummy, // 다른 배경 에셋 활용
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
    val messageChatRooms = listOf(
        ChatRoomItem(
            id = "chat_1",
            name = "김민수",
            lastMessage = "아 ㅋㅋ 그치 근데 그건 좀;;",
            timestamp = "2h",
            isUnread = true,
            avatarUrl = "" // 필요한 경우 Res.drawable.img_profile_dummy_1 등으로 리소스 식별자 연결 가능
        ),
        ChatRoomItem(
            id = "chat_2",
            name = "박지연",
            lastMessage = "나? 집이지",
            timestamp = "5h",
            isUnread = true,
            avatarUrl = ""
        ),
        ChatRoomItem(
            id = "chat_3",
            name = "이준호",
            lastMessage = "2시간 전 보냄",
            timestamp = "Yesterday",
            isUnread = false,
            avatarUrl = ""
        ),
        ChatRoomItem(
            id = "chat_4",
            name = "최수연",
            lastMessage = "2일 전에 보냄",
            timestamp = "Mon",
            isUnread = false,
            avatarUrl = ""
        ),
        ChatRoomItem(
            id = "chat_5",
            name = "정현우",
            lastMessage = "12일 전에 보냄",
            timestamp = "Sun",
            isUnread = false,
            avatarUrl = ""
        )
    )

    /**
     * 시안의 '방명록 탭' 전용 테스트 데이터 리스트
     * 규칙: 고인 접두사 '故' 및 이름 옆 '🌸' 지시선 결합용 스펙
     */
    val messageGuestBooks = listOf(
        GuestBookItem(
            id = "book_1",
            deceasedName = "김영희",
            recentContent = "영원히 기억하겠습니다. 하늘에서 편히 쉬세요...",
            relativeTime = "1년 전",
            avatarUrl = ""
        ),
        GuestBookItem(
            id = "book_2",
            deceasedName = "이철수",
            recentContent = "당신의 따뜻한 미소가 늘 그립습니다.",
            relativeTime = "8개월 전",
            avatarUrl = ""
        ),
        GuestBookItem(
            id = "book_3",
            deceasedName = "박순자",
            recentContent = "삼가 고인의 명복을 빕니다. 좋은 곳으로 가셨기를..",
            relativeTime = "6개월 전",
            avatarUrl = ""
        ),
        GuestBookItem(
            id = "book_4",
            deceasedName = "최민준",
            recentContent = "함께했던 시간들이 제겐 너무나도 소중한 추억입니다.",
            relativeTime = "3개월 전",
            avatarUrl = ""
        ),
        GuestBookItem(
            id = "book_5",
            deceasedName = "정혜진",
            recentContent = "당신의 이야기는 항상 우리 마음속에 남아있을 거예요.",
            relativeTime = "2주 전",
            avatarUrl = ""
        )
    )

    val profileUsername = "hong_gild"
    val profileDisplayName = "홍길동"
    val profileImage = Res.drawable.img_profile_dummy_1
    val profileBgImage = Res.drawable.img_memorial_bg_dummy
    val posthumousFeedCount = 5
    val followersCount = 248
    val followingCount = 91
    val scheduledFeedCount = 3

    val profileFeeds = listOf(
        ProfileFeedItem(
            id = "1",
            title = "가을 산책을 하며",
            subtitle = "예전에도 요즘에도 산책하는걸 좋아하는데요 오늘은 북한산으로 산책을 갔답니..",
            thumbnail = Res.drawable.img_post_dummy3,
            likes = 24,
            comments = 6
        ),
        ProfileFeedItem(
            id = "2",
            title = "요즘 내가 좋아하는 것들",
            subtitle = "여러분들은 어떤 취미를 가지고 계신가요? 저는 최근에 독서와 음악 감상에..",
            thumbnail = Res.drawable.img_post_dummy1,
            likes = 42,
            comments = 11
        ),
        ProfileFeedItem(
            id = "3",
            title = "세 번째 기록",
            subtitle = "소중한 기억들을 여기에 차곡차곡 남겨둡니다. 나중에 꺼내볼 수 있도록..",
            thumbnail = Res.drawable.img_post_dummy2,
            likes = 15,
            comments = 2
        )
    )
}