package com.gentlelady.reborn.feedwrite.domain.usecase

import com.gentlelady.reborn.feedwrite.domain.repository.FeedWriteRepository
import com.gentlelady.reborn.feedwrite.presentation.FeedWriteResult
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.Instant

class CreatePostUseCase(
    private val repository: FeedWriteRepository
) {
    suspend operator fun invoke(
        caption: String,
        images: List<ByteArray>,
        isPosthumous: Boolean,
        publishAt: Instant?,
    ): FeedWriteResult {
        if (caption.isBlank() && images.isEmpty()) return FeedWriteResult.SubmitFailed(EMPTY_POST_MESSAGE)
        return try {
            repository.createPost(caption.trim(), images, isPosthumous, publishAt)
            FeedWriteResult.Submitted
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            FeedWriteResult.SubmitFailed(SUBMIT_FAILED_MESSAGE)
        }
    }
}

private const val EMPTY_POST_MESSAGE = "내용이나 사진을 하나 이상 넣어주세요."
private const val SUBMIT_FAILED_MESSAGE = "게시글을 올리지 못했어요. 로그인 상태와 네트워크를 확인해주세요."
