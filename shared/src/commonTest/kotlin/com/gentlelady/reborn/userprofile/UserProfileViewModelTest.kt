package com.gentlelady.reborn.userprofile

import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.interaction.data.InteractionRepository
import com.gentlelady.reborn.interaction.domain.SetLikeUseCase
import com.gentlelady.reborn.interaction.presentation.LikeSync
import com.gentlelady.reborn.userprofile.data.UserProfileRepository
import com.gentlelady.reborn.userprofile.domain.FollowRejectedException
import com.gentlelady.reborn.userprofile.domain.FollowTab
import com.gentlelady.reborn.userprofile.domain.FollowUser
import com.gentlelady.reborn.userprofile.domain.GetRelationsUseCase
import com.gentlelady.reborn.userprofile.presentation.FollowListIntent
import com.gentlelady.reborn.userprofile.presentation.FollowListViewModel
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
        val relationCalls = mutableListOf<Pair<FollowTab, Int>>()
        override suspend fun relations(userId: String, tab: FollowTab, offset: Int): Pair<List<FollowUser>, Int?> {
            relationCalls += tab to offset
            // 팔로워 탭: 2명 + 다음 페이지 / 팔로잉 탭: "x" 1명(팔로워 탭에도 있음)
            return when (tab) {
                FollowTab.FOLLOWERS -> if (offset == 0) listOf(user("x"), user("y")) to 2 else listOf(user("z")) to null
                FollowTab.FOLLOWING -> listOf(user("x")) to null
            }
        }
        private fun user(id: String) = FollowUser(id, id, id, null, followedByMe = false, isMe = false)
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

    // ---------- 팔로워·팔로잉 목록 ----------

    @Test
    fun followListLoadsTabsLazilyAndPages() = runTest(dispatcher) {
        val repo = FakeRepo(emptyMap())
        val vm = FollowListViewModel(GetRelationsUseCase(repo), SetFollowUseCase(repo))
        vm.onIntent(FollowListIntent.Open("u", FollowTab.FOLLOWERS))
        runCurrent()
        assertEquals(listOf("x", "y"), vm.state.value.current.users.map { it.id })

        vm.onIntent(FollowListIntent.LoadMore)
        runCurrent()
        assertEquals(listOf("x", "y", "z"), vm.state.value.current.users.map { it.id })

        vm.onIntent(FollowListIntent.SelectTab(FollowTab.FOLLOWING))
        runCurrent()
        vm.onIntent(FollowListIntent.SelectTab(FollowTab.FOLLOWERS)) // 이미 받은 탭은 다시 안 받는다
        vm.onIntent(FollowListIntent.Open("u", FollowTab.FOLLOWING)) // 같은 사람 다시 열기도
        runCurrent()
        assertEquals(listOf(FollowTab.FOLLOWERS to 0, FollowTab.FOLLOWERS to 2, FollowTab.FOLLOWING to 0), repo.relationCalls)
    }

    @Test
    fun followInListUpdatesSamePersonInBothTabs() = runTest(dispatcher) {
        val repo = FakeRepo(emptyMap())
        val vm = FollowListViewModel(GetRelationsUseCase(repo), SetFollowUseCase(repo))
        vm.onIntent(FollowListIntent.Open("u", FollowTab.FOLLOWERS))
        runCurrent()
        vm.onIntent(FollowListIntent.SelectTab(FollowTab.FOLLOWING))
        runCurrent()

        vm.onIntent(FollowListIntent.ToggleFollow("x"))
        runCurrent()
        repo.followReplies[0].complete(true to 1)
        runCurrent()
        val pages = vm.state.value.pages
        assertTrue(pages.getValue(FollowTab.FOLLOWERS).users.first { it.id == "x" }.followedByMe)
        assertTrue(pages.getValue(FollowTab.FOLLOWING).users.first { it.id == "x" }.followedByMe)
    }
}
