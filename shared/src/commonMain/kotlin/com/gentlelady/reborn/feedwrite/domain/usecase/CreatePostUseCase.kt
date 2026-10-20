package com.gentlelady.reborn.feedwrite.domain.usecase

import com.gentlelady.reborn.feedwrite.domain.repository.FeedWriteRepository
import com.gentlelady.reborn.feedwrite.domain.repository.ImagesExpiredException
import com.gentlelady.reborn.feedwrite.presentation.FeedWriteResult
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.Instant

class CreatePostUseCase(
    private val repository: FeedWriteRepository
) {
    /**
     * [uploadedImageUrls] 가 있으면(지난 시도에서 사진은 다 올라가고 글 작성만 실패) 다시 올리지 않고 재사용한다.
     * 같은 [requestId] 로 재시도하면 서버가 글을 한 번만 만든다.
     */
    suspend operator fun invoke(
        caption: String,
        images: List<ByteArray>,
        uploadedImageUrls: List<String>?,
        isPosthumous: Boolean,
        publishAt: Instant?,
        requestId: String,
    ): FeedWriteResult {
        if (caption.isBlank() && images.isEmpty()) return FeedWriteResult.SubmitFailed(EMPTY_POST_MESSAGE)

        val imageUrls = uploadedImageUrls ?: try {
            repository.uploadImages(images)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return FeedWriteResult.SubmitFailed(UPLOAD_FAILED_MESSAGE)
        }

        return try {
            repository.createPost(caption.trim(), imageUrls, isPosthumous, publishAt, requestId)
            FeedWriteResult.Submitted
        } catch (e: CancellationException) {
            throw e
        } catch (e: ImagesExpiredException) {
            // 올려 둔 사진을 서버가 더는 안 받음 → 캐시를 버려 다음 시도에서 다시 올린다
            FeedWriteResult.SubmitFailed(SUBMIT_FAILED_MESSAGE, uploadedImageUrls = null)
        } catch (e: Exception) {
            FeedWriteResult.SubmitFailed(SUBMIT_FAILED_MESSAGE, uploadedImageUrls = imageUrls)
        }
    }
}

private const val EMPTY_POST_MESSAGE = "내용이나 사진을 하나 이상 넣어주세요."
private const val UPLOAD_FAILED_MESSAGE = "사진을 올리지 못했어요. 네트워크를 확인하고 다시 시도해주세요."
private const val SUBMIT_FAILED_MESSAGE = "게시글을 올리지 못했어요. 로그인 상태와 네트워크를 확인해주세요."
