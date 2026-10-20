package com.gentlelady.reborn.myprofile.domain.usecase

import com.gentlelady.reborn.myprofile.data.MyProfileRepository
import kotlinx.coroutines.CancellationException

/** 서버 profileTextError 와 같은 한도 — 서버가 최종 검사, 앱은 미리 막아 빠르게 알려준다. */
const val MAX_DISPLAY_NAME = 20
const val MAX_BIO = 100

class UpdateProfileTextUseCase(
    private val repository: MyProfileRepository
) {
    sealed interface Result {
        data class Updated(val displayName: String, val bio: String) : Result
        data class Failed(val message: String) : Result
    }

    /** [displayName] / [bio] 중 바꿀 것만 넘긴다 (null = 안 바꿈). 앞뒤 공백은 지운다. */
    suspend operator fun invoke(displayName: String? = null, bio: String? = null): Result {
        val name = displayName?.trim()
        val about = bio?.trim()
        when {
            name != null && name.isEmpty() -> return Result.Failed("닉네임을 입력해주세요.")
            name != null && name.length > MAX_DISPLAY_NAME -> return Result.Failed("닉네임은 ${MAX_DISPLAY_NAME}자까지 쓸 수 있어요.")
            about != null && about.length > MAX_BIO -> return Result.Failed("한 줄 소개는 ${MAX_BIO}자까지 쓸 수 있어요.")
        }
        return try {
            val profile = repository.updateText(name, about)
            Result.Updated(displayName = profile.displayName, bio = profile.bio.orEmpty())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failed("저장하지 못했어요. 로그인 상태와 네트워크를 확인해주세요.")
        }
    }
}
