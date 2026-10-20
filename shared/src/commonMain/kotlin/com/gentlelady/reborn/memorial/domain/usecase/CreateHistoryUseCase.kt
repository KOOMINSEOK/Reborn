package com.gentlelady.reborn.memorial.domain.usecase

import com.gentlelady.reborn.memorial.data.MemorialRepository
import com.gentlelady.reborn.memorial.presentation.MemorialHistoryItem
import kotlinx.coroutines.CancellationException

class CreateHistoryUseCase(
    private val repository: MemorialRepository
) {
    sealed interface Result {
        data class Posted(val item: MemorialHistoryItem) : Result
        data class Failed(val message: String) : Result
    }

    suspend operator fun invoke(memorialId: String, caption: String, image: ByteArray): Result = try {
        Result.Posted(repository.createHistory(memorialId, caption.trim(), image))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.Failed(POST_FAILED_MESSAGE)
    }
}

private const val POST_FAILED_MESSAGE = "추억을 올리지 못했어요. 로그인 상태와 네트워크를 확인해주세요."
