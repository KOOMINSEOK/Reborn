//package com.gentlelady.reborn.search.domain.mapper
//
//import com.gentlelady.reborn.search.presentation.SearchItem
//import com.gentlelady.reborn.home.domain.model.HomePost
//import com.gentlelady.reborn.memorial_swipe.domain.model.MemorialItem
//import com.gentlelady.reborn.memorial_swipe.domain.model.MusicItem
//
///**
// * SearchItem -> HomePost 변환 확장 함수
// * SearchScreen의 ItemType.POST 렌더링에 매핑됨
// */
//fun SearchItem.toHomePost(): HomePost {
//    return HomePost(
//        id = this.id,
//        authorName = this.title,               // SearchScreen 스펙: title -> authorName
//        authorProfileUrl = this.profileImage,  // 동적 이미지 리소스 (설정되지 않은 경우 null)
//        contentImageUrl = this.contentImage,   // 동적 이미지 리소스 (설정되지 않은 경우 null)
//        caption = this.subtitle,               // SearchScreen 스펙: subtitle -> caption
//        isPosthumous = this.isPosthumous,      // 사후 게시글 여부 플래그 매핑
//        isLocked = false,
//        likes = 0,                             // 필요 시 SearchItem에 기본 데이터 필드 추가 가능
//        comments = 0,
//        postedAt = this.postedAt.ifEmpty { "2026.01.01" }
//    )
//}
//
///**
// * SearchItem -> MemorialItem 변환 확장 함수
// * SearchScreen의 ItemType.MEMORIAL 렌더링에 매핑됨
// */
//fun SearchItem.toMemorialItem(): MemorialItem {
//    return MemorialItem(
//        id = this.id,
//        rank = this.rank,                      // MemorialProfileItem 내부 순위 배지 대응용 (기본값 0 또는 데이터 연동)
//        name = this.title,                     // SearchScreen 스펙: title -> name
//        jobTitle = this.subtitle,              // SearchScreen 스펙: subtitle -> jobTitle (예: 소방관)
//        location = this.location.ifEmpty { "서울" }, // 지역 정보 연동
//        flowerCount = this.flowerCount.ifEmpty { "0" }, // 꽃 개수 카운트 문자열 매핑
//        birthDate = this.birthDate.ifEmpty { "1990" },
//        deathDate = this.deathDate.ifEmpty { "2024" },
//        profileImageUrl = this.profileImage,   // 동적 이미지 리소스 매핑
//        backgroundImageUrl = this.contentImage, // 동적 이미지 리소스 매핑
//        currentMusic = null
//    )
//}