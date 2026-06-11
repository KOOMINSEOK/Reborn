package com.gentlelady.reborn.home.data.repository

import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.home.domain.repository.HomeRepository
import reborn.shared.generated.resources.Res
import reborn.shared.generated.resources.img_memorial_profile_dummy
import reborn.shared.generated.resources.img_post_dummy1
import reborn.shared.generated.resources.img_post_dummy2

class HomeRepositoryImpl : HomeRepository {
    override suspend fun getHomeFeed(): List<HomePost> {
        // 디자인 이미지에 맞춘 가짜 데이터 생성
        return listOf(
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
                authorProfileUrl = Res.drawable.img_memorial_profile_dummy,
                contentImageUrl = Res.drawable.img_post_dummy2,
                isLocked = true,
                likes = 150,
                comments = 45,
                postedAt = "2026.01.03"
            )
        )
    }
}