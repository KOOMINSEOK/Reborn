package com.gentlelady.reborn.memorial.domain.usecase

import com.gentlelady.reborn.memorial.data.MemorialRepository
import com.gentlelady.reborn.memorial.presentation.MemorialGuestBookItem
import kotlinx.coroutines.CancellationException

class CreateGuestbookEntryUseCase(
    private val repository: MemorialRepository
) {
    sealed interface Result {
        data class Posted(val item: MemorialGuestBookItem) : Result
        data class Failed(val message: String) : Result
    }

    suspend operator fun invoke(memorialId: String, message: String): Result {
        val trimmed = message.trim()
        if (trimmed.isEmpty()) return Result.Failed(EMPTY_MESSAGE)
        return try {
            Result.Posted(repository.createGuestbookEntry(memorialId, trimmed))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failed(POST_FAILED_MESSAGE)
        }
    }
}

private const val EMPTY_MESSAGE = "남길 말을 입력해주세요."
private const val POST_FAILED_MESSAGE = "방명록을 남기지 못했어요. 로그인 상태와 네트워크를 확인해주세요."
