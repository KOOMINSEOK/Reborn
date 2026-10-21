package com.gentlelady.reborn.userprofile.di

import com.gentlelady.reborn.userprofile.data.UserProfileRepository
import com.gentlelady.reborn.userprofile.data.UserProfileRepositoryImpl
import com.gentlelady.reborn.userprofile.domain.GetRelationsUseCase
import com.gentlelady.reborn.userprofile.domain.GetUserPostsUseCase
import com.gentlelady.reborn.userprofile.presentation.FollowListViewModel
import com.gentlelady.reborn.userprofile.domain.GetUserProfileUseCase
import com.gentlelady.reborn.userprofile.domain.SetFollowUseCase
import com.gentlelady.reborn.userprofile.presentation.UserProfileViewModel
import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import org.koin.dsl.module

val userProfileModule = module {
    single<UserProfileRepository> { UserProfileRepositoryImpl(get(), get()) }
    factory { GetUserProfileUseCase(get()) }
    factory { GetUserPostsUseCase(get()) }
    factory { SetFollowUseCase(get()) }
    factory { GetRelationsUseCase(get()) }
    factory { UserProfileViewModel(get(), get(), get(), get()) }
    factory { FollowListViewModel(get(), get(), CurrentUserIdUseCase(get())) }
}
