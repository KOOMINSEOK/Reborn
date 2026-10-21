package com.gentlelady.reborn.userprofile

import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.interaction.data.InteractionRepository
import com.gentlelady.reborn.interaction.domain.SetLikeUseCase
import com.gentlelady.reborn.interaction.presentation.LikeSync
import com.gentlelady.reborn.userprofile.data.UserProfileRepository
import com.gentlelady.reborn.userprofile.domain.FollowRejectedException
import com.gentlelady.reborn.userprofile.domain.GetUserPostsUseCase
import com.gentlelady.reborn.userprofile.domain.GetUserProfileUseCase
import com.gentlelady.reborn.userprofile.domain.SetFollowUseCase
import com.gentlelady.reborn.userprofile.domain.UserProfile
import com.gentlelady.reborn.userprofile.presentation.UserProfileIntent
import com.gentlelady.reborn.userprofile.presentation.UserProfileViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    private fun profile(id: String, private: Boolean = false) =
        UserProfile(id, "h_$id", "이름$id", "", null, private, followerCount = 10, followingCount = 3,
            followedByMe = false, blockedByMe = false, isMe = false)

    private class FakeRepo(val profiles: Map<String, UserProfile>) : UserProfileRepository {
        val followReplies = mutableListOf<CompletableDeferred<Pair<Boolean, Int>>>()
        val profileReplies = mutableMapOf<String, CompletableDeferred<UserProfile>>()
        override suspend fun profile(userId: String) =
            profileReplies[userId]?.await() ?: profiles.getValue(userId)
        override suspend fun posts(userId: String, offset: Int): Pair<List<HomePost>, Int?> =
            listOf(HomePost(id = "p_$userId", authorId = userId, authorName = "x", caption = "", postedAt = "")) to null
        override suspend fun setFollow(userId: String, follow: Boolean) =
            CompletableDeferred<Pair<Boolean, Int>>().also { followReplies += it }.await()
    }

    private object NoLikes : InteractionRepository {
        override suspend fun setLike(target: InteractionTarget, id: String, liked: Boolean) = error("unused")
        override suspend fun comments(target: InteractionTarget, id: String, offset: Int) = error("unused")
        override suspend fun addComment(target: InteractionTarget, id: String, body: String) = error("unused")
        override suspend fun deleteComment(target: InteractionTarget, commentId: String) = error("unused")
    }

    private fun vm(repo: FakeRepo) = UserProfileViewModel(
        GetUserProfileUseCase(repo), GetUserPostsUseCase(repo), SetFollowUseCase(repo), LikeSync(SetLikeUseCase(NoLikes)),
    )

    @Test
    fun followIsOptimisticThenServerCount() = runTest(dispatcher) {
        val repo = FakeRepo(mapOf("u" to profile("u")))
        val vm = vm(repo)
        vm.onIntent(UserProfileIntent.Load("u"))
        runCurrent()

        vm.onIntent(UserProfileIntent.ToggleFollow)
        assertTrue(vm.state.value.profile!!.followedByMe)
        assertEquals(11, vm.state.value.profile!!.followerCount)

        runCurrent()
        repo.followReplies[0].complete(true to 15) // 그 사이 다른 사람들도 팔로우
        runCurrent()
        assertEquals(15, vm.state.value.profile!!.followerCount)
    }

    @Test
    fun rejectedFollowRevertsAndExplains() = runTest(dispatcher) {
        val repo = FakeRepo(mapOf("u" to profile("u", private = true)))
        val vm = vm(repo)
        vm.onIntent(UserProfileIntent.Load("u"))
        runCurrent()

        vm.onIntent(UserProfileIntent.ToggleFollow)
        runCurrent()
        repo.followReplies[0].completeExceptionally(FollowRejectedException("private_account"))
        runCurrent()

        val s = vm.state.value
        assertFalse(s.profile!!.followedByMe)
        assertEquals(10, s.profile!!.followerCount)
        assertEquals("비공개 계정은 아직 팔로우할 수 없어요.", s.message)
        vm.onIntent(UserProfileIntent.MessageShown)
        assertNull(vm.state.value.message)
    }

    @Test
    fun lateProfileOfPreviousUserIsIgnored() = runTest(dispatcher) {
        val repo = FakeRepo(mapOf("a" to profile("a"), "b" to profile("b")))
        repo.profileReplies["a"] = CompletableDeferred()
        val vm = vm(repo)
        vm.onIntent(UserProfileIntent.Load("a"))
        runCurrent()
        vm.onIntent(UserProfileIntent.Load("b"))
        runCurrent()
        repo.profileReplies.getValue("a").complete(profile("a"))
        runCurrent()

        assertEquals("b", vm.state.value.profile!!.id)
        assertEquals(listOf("p_b"), vm.state.value.posts.map { it.id })
    }
}
