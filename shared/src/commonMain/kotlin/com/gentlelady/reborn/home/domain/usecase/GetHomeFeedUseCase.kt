package com.gentlelady.reborn.home.domain.usecase

import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.home.domain.repository.HomeRepository

class GetHomeFeedUseCase(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(): List<HomePost> {
        return repository.getHomeFeed()
    }
}