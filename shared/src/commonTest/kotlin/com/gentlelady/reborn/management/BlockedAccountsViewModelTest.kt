package com.gentlelady.reborn.management

import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.core.auth.Session
import com.gentlelady.reborn.core.auth.SessionStore
import com.gentlelady.reborn.data.BlockedAccountsMockData
import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.management.security.blocked_accounts.domain.model.BlockedAccountItem
import com.gentlelady.reborn.management.security.blocked_accounts.presentation.BlockedAccountsIntent
import com.gentlelady.reborn.management.security.blocked_accounts.presentation.BlockedAccountsViewModel
import com.gentlelady.reborn.userprofile.data.UserProfileRepository
import com.gentlelady.reborn.userprofile.domain.FollowTab
import com.gentlelady.reborn.userprofile.domain.FollowUser
import com.gentlelady.reborn.userprofile.domain.GetBlockedUsersUseCase
import com.gentlelady.reborn.userprofile.domain.SetBlockUseCase
import com.gentlelady.reborn.userprofile.domain.UserProfile
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BlockedAccountsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    private class FakeRepo : UserProfileRepository {
        var blocked = listOf(item("a"), item("b"))
        val unblocks = mutableListOf<Pair<String, CompletableDeferred<Unit>>>()
        override suspend fun blockedUsers() = blocked
        override suspend fun setBlock(userId: String, block: Boolean) =
            CompletableDeferred<Unit>().also { unblocks += userId to it }.await()
        override suspend fun profile(userId: String): UserProfile = error("unused")
        override suspend fun posts(userId: String, offset: Int): Pair<List<HomePost>, Int?> = error("unused")
        override suspend fun relations(userId: String, tab: FollowTab, offset: Int): Pair<List<FollowUser>, Int?> = error("unused")
        override suspend fun setFollow(userId: String, follow: Boolean): Pair<Boolean, Int> = error("unused")
    }

    private companion object {
        fun item(id: String) = BlockedAccountItem(id, "이름$id", "h_$id", avatar = null)
    }

    private fun vm(repo: FakeRepo, me: String?) = BlockedAccountsViewModel(
        GetBlockedUsersUseCase(repo), SetBlockUseCase(repo),
        CurrentUserIdUseCase(SessionStore().apply { if (me != null) current = Session("AT", "RT", 0, userId = me) }),
    )

    @Test
    fun loggedInShowsServerListAndUnblockRemovesAfterServer() = runTest(dispatcher) {
        val repo = FakeRepo()
        val vm = vm(repo, me = "me")
        vm.handleIntent(BlockedAccountsIntent.LoadBlockedAccounts)
        runCurrent()
        assertEquals(listOf("a", "b"), vm.state.value.accounts.map { it.id })

        vm.handleIntent(BlockedAccountsIntent.ClickUnblock("a"))
        vm.handleIntent(BlockedAccountsIntent.ConfirmUnblock)
        runCurrent()
        assertTrue("a" in vm.state.value.unblockingIds)
        assertEquals(listOf("a", "b"), vm.state.value.accounts.map { it.id }) // 서버 확인 전엔 남아 있다

        repo.unblocks[0].second.complete(Unit)
        runCurrent()
        assertEquals(listOf("b"), vm.state.value.accounts.map { it.id })
    }

    @Test
    fun failedUnblockKeepsRowAndExplains() = runTest(dispatcher) {
        val repo = FakeRepo()
        val vm = vm(repo, me = "me")
        vm.handleIntent(BlockedAccountsIntent.LoadBlockedAccounts)
        runCurrent()
        vm.handleIntent(BlockedAccountsIntent.ClickUnblock("a"))
        vm.handleIntent(BlockedAccountsIntent.ConfirmUnblock)
        runCurrent()
        repo.unblocks[0].second.completeExceptionally(IllegalStateException("down"))
        runCurrent()

        assertEquals(listOf("a", "b"), vm.state.value.accounts.map { it.id })
        assertTrue(vm.state.value.unblockingIds.isEmpty())
        assertEquals("차단을 풀지 못했어요. 잠시 후 다시 시도해 주세요.", vm.state.value.message)
    }

    @Test
    fun reopeningPicksUpNewBlocksAndBrowseModeUsesMock() = runTest(dispatcher) {
        val repo = FakeRepo()
        val vm = vm(repo, me = "me")
        vm.handleIntent(BlockedAccountsIntent.LoadBlockedAccounts)
        runCurrent()
        repo.blocked = listOf(item("c")) + repo.blocked // 프로필에서 방금 차단
        vm.handleIntent(BlockedAccountsIntent.LoadBlockedAccounts)
        runCurrent()
        assertEquals(listOf("c", "a", "b"), vm.state.value.accounts.map { it.id })

        val browse = vm(FakeRepo(), me = null)
        browse.handleIntent(BlockedAccountsIntent.LoadBlockedAccounts)
        assertEquals(BlockedAccountsMockData.blockedAccounts, browse.state.value.accounts)
        assertNull(browse.state.value.message)
    }
}
