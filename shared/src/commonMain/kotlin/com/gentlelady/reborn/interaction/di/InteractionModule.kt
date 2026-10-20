package com.gentlelady.reborn.interaction.di

import com.gentlelady.reborn.interaction.data.InteractionRepository
import com.gentlelady.reborn.interaction.data.InteractionRepositoryImpl
import com.gentlelady.reborn.interaction.domain.AddCommentUseCase
import com.gentlelady.reborn.interaction.domain.DeleteCommentUseCase
import com.gentlelady.reborn.interaction.domain.GetCommentsUseCase
import com.gentlelady.reborn.interaction.domain.SetLikeUseCase
import com.gentlelady.reborn.interaction.presentation.CommentsViewModel
import com.gentlelady.reborn.interaction.presentation.LikeSync
import org.koin.dsl.module

val interactionModule = module {
    single<InteractionRepository> { InteractionRepositoryImpl(get(), get()) }
    factory { SetLikeUseCase(get()) }
    factory { GetCommentsUseCase(get()) }
    factory { AddCommentUseCase(get()) }
    factory { DeleteCommentUseCase(get()) }
    factory { LikeSync(get()) } // ViewModel 마다 하나
    factory { CommentsViewModel(get(), get(), get()) }
}
