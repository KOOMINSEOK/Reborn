package com.gentlelady.reborn.search.data

import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.SearchResultDto
import com.gentlelady.reborn.core.network.dto.toAbsoluteServerUrl
import com.gentlelady.reborn.core.network.dto.toHomePost
import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.search.domain.entity.MemorialSearchItem
import com.gentlelady.reborn.search.presentation.SearchTab

/** 검색. 목업 폴백 없음 — 실패는 예외 (화면이 알려준다). */
interface SearchRepository {
    suspend fun search(query: String, sort: SearchTab): List<MemorialSearchItem>
    suspend fun randomPosts(): List<HomePost>
}

class SearchRepositoryImpl(private val api: RebornApi) : SearchRepository {
    override suspend fun search(query: String, sort: SearchTab) =
        api.search(query, sort.serverName).items.mapIndexed { i, it -> it.toItem(rank = i + 1) }

    override suspend fun randomPosts() = api.randomPosts().items.map { it.toHomePost() }
}

private val SearchTab.serverName
    get() = when (this) {
        SearchTab.LIKES -> "likes"
        SearchTab.VIEWS -> "views"
        SearchTab.FLOWERS -> "flowers"
    }

private fun SearchResultDto.toItem(rank: Int): MemorialSearchItem {
    val memorial = kind == "memorial"
    return MemorialSearchItem(
        id = id,
        rank = rank,
        name = name,
        birthDate = "",
        deathDate = "",
        location = "",
        flowerCount = compactCount(flowers),
        profileImageUrl = null,
        imageUrl = imageUrl.toAbsoluteServerUrl(),
        // 추모 페이지 = 고인 (故), 사람 = 프로필
        isDeceased = memorial,
        hasProfile = !memorial,
        hasMemorial = memorial,
        likeValue = likes,
        viewValue = views,
        flowerValue = flowers,
    )
}

/** 1234 → "1.2k", 24800 → "24.8k", 12000 → "12k", 100k 넘으면 소수점 없이 */
internal fun compactCount(n: Long): String = when {
    n < 1_000 -> n.toString()
    n < 1_000_000 -> (n / 100).let { t -> if (t % 10 == 0L || t >= 1000) "${t / 10}k" else "${t / 10}.${t % 10}k" }
    else -> (n / 100_000).let { t -> if (t % 10 == 0L || t >= 1000) "${t / 10}m" else "${t / 10}.${t % 10}m" }
}
