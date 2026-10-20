package com.gentlelady.reborn.myprofile.domain.usecase

import com.gentlelady.reborn.core.network.dto.toAbsoluteServerUrl
import com.gentlelady.reborn.myprofile.data.MyProfileRepository
import kotlinx.coroutines.CancellationException

class UpdateAvatarUseCase(
    private val repository: MyProfileRepository
) {
    sealed interface Result {
        /** [avatarUrl] = 바뀐 아바타의 절대 URL */
        data class Updated(val avatarUrl: String?) : Result
        data class Failed(val message: String) : Result
    }

    suspend operator fun invoke(jpeg: ByteArray): Result = try {
        Result.Updated(repository.updateAvatar(jpeg).avatarUrl.toAbsoluteServerUrl())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.Failed(UPDATE_FAILED_MESSAGE)
    }
}

private const val UPDATE_FAILED_MESSAGE = "프로필 사진을 바꾸지 못했어요. 로그인 상태와 네트워크를 확인해주세요."
