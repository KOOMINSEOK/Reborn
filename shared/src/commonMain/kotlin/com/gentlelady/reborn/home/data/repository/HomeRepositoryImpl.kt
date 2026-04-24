package com.gentlelady.reborn.home.data.repository

import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.home.domain.repository.HomeRepository

class HomeRepositoryImpl : HomeRepository {
    override suspend fun getHomeFeed(): List<HomePost> {
        // 디자인 이미지에 맞춘 가짜 데이터 생성
        return listOf(
            HomePost(
                id = "1",
                authorName = "홍길동",
                authorProfileUrl = "", // 나중에 이미지 리소스 연결
                contentImageUrl = "",
                caption = "설날을 맞아 북한산으로 등산을 다녀왔습니다. 여러분은 새해 목표로 어떤 목표를 세우셨나요? 저 같...",
                likes = 128,
                comments = 34,
                postedAt = "방금 전"
            ),
            HomePost(
                id = "2",
                authorName = "김첨지",
                authorProfileUrl = "",
                contentImageUrl = "",
                caption = "사후 게시글 — 작성자가 미리 예약해 두고 사망 후 공개되는 게시글",
                isPosthumous = true, // 디자인의 파란 박스 활성화
                isLocked = true,
                likes = 2400,
                comments = 312,
                postedAt = "1일 전"
            )
        )
    }
}