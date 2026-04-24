package com.gentlelady.reborn.home.domain.repository
import com.gentlelady.reborn.home.domain.model.HomePost

interface HomeRepository {
    suspend fun getHomeFeed(): List<HomePost>
}