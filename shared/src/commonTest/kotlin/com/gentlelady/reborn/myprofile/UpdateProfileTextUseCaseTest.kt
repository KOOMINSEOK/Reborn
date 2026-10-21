package com.gentlelady.reborn.myprofile

import com.gentlelady.reborn.core.network.dto.MyProfileDto
import com.gentlelady.reborn.myprofile.data.MyProfileRepository
import com.gentlelady.reborn.myprofile.domain.usecase.MAX_BIO
import com.gentlelady.reborn.myprofile.domain.usecase.MAX_DISPLAY_NAME
import com.gentlelady.reborn.myprofile.domain.usecase.UpdateProfileTextUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class UpdateProfileTextUseCaseTest {

    private class FakeRepo : MyProfileRepository {
        val calls = mutableListOf<Pair<String?, String?>>()
        override suspend fun getMyProfile(): MyProfileDto? = null
        override suspend fun updateAvatar(jpeg: ByteArray): MyProfileDto = error("unused")
        override suspend fun myPosts(userId: String, limit: Int) = error("unused")
        override suspend fun updateText(displayName: String?, bio: String?): MyProfileDto {
            calls += displayName to bio
            return MyProfileDto(id = "u", handle = "h", displayName = displayName ?: "old", bio = bio)
        }
    }

    @Test
    fun trimsAndSendsOnlyChangedField() = runTest {
        val repo = FakeRepo()
        val result = UpdateProfileTextUseCase(repo)(displayName = "  홍길동 ")
        assertEquals(listOf<Pair<String?, String?>>("홍길동" to null), repo.calls)
        assertEquals("홍길동", (result as UpdateProfileTextUseCase.Result.Updated).displayName)
    }

    @Test
    fun rejectsInvalidTextWithoutCallingServer() = runTest {
        val repo = FakeRepo()
        val useCase = UpdateProfileTextUseCase(repo)
        assertIs<UpdateProfileTextUseCase.Result.Failed>(useCase(displayName = "   "))
        assertIs<UpdateProfileTextUseCase.Result.Failed>(useCase(displayName = "가".repeat(MAX_DISPLAY_NAME + 1)))
        assertIs<UpdateProfileTextUseCase.Result.Failed>(useCase(bio = "가".repeat(MAX_BIO + 1)))
        assertTrue(repo.calls.isEmpty())
    }
}
