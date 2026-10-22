package com.gentlelady.reborn.core.network.dto

import kotlinx.serialization.Serializable

/** 서버 `GET /search` 한 줄 (사람 또는 추모 페이지) */
@Serializable
data class SearchResultDto(
    /** user · memorial */
    val kind: String,
    val id: String,
    val name: String,
    val handle: String,
    val imageUrl: String? = null,
    val likes: Long = 0,
    val views: Long = 0,
    val flowers: Long = 0,
)

@Serializable
data class SearchListDto(val items: List<SearchResultDto>)
