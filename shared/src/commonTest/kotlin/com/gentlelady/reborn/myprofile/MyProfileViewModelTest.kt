package com.gentlelady.reborn.myprofile

import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.core.auth.Session
import com.gentlelady.reborn.core.auth.SessionStore
import com.gentlelady.reborn.core.network.dto.MyProfileDto
import com.gentlelady.reborn.data.MockDataSource
import com.gentlelady.reborn.myprofile.data.MyProfileRepository
import com.gentlelady.reborn.myprofile.domain.model.MyProfileFeedItem
import com.gentlelady.reborn.myprofile.domain.usecase.UpdateAvatarUseCase
import com.gentlelady.reborn.myprofile.domain.usecase.UpdateProfileTextUseCase
import com.gentlelady.reborn.myprofile.presentation.MyProfileViewModel
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
class MyProfileViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    private class FakeRepo(val store: SessionStore) : MyProfileRepository {
        val posts = mutableListOf<CompletableDeferred<List<MyProfileFeedItem>>>()
        override suspend fun getMyProfile(): MyProfileDto? =
            store.current?.userId?.let { MyProfileDto(id = it, handle = "h_$it", displayName = "이름$it") }
        override suspend fun myPosts(userId: String, limit: Int) =
            CompletableDeferred<List<MyProfileFeedItem>>().also { posts += it }.await()
        override suspend fun updateAvatar(jpeg: ByteArray): MyProfileDto = error("unused")
        override suspend fun updateText(displayName: String?, bio: String?): MyProfileDto = error("unused")
    }

    private fun vm(store: SessionStore, repo: FakeRepo) =
        MyProfileViewModel(repo, UpdateAvatarUseCase(repo), UpdateProfileTextUseCase(repo), CurrentUserIdUseCase(store))

    private fun item(id: String) = MyProfileFeedItem(id, "제목", "부제")

    @Test
    fun loggedInUserNeverSeesMockPostsEvenIfPostsFail() = runTest(dispatcher) {
        val store = SessionStore().apply { current = Session("AT", "RT", 0, userId = "u1") }
        val repo = FakeRepo(store)
        val vm = vm(store, repo)
        runCurrent()
        assertEquals("u1", vm.state.value.myUserId)
        assertTrue(vm.state.value.feeds.isEmpty()) // 글을 받기 전에도 목업이 아님

        repo.posts.last().completeExceptionally(IllegalStateException("timeout"))
        runCurrent()
        assertTrue(vm.state.value.feeds.isEmpty())
    }

    @Test
    fun lateResponseOfPreviousAccountIsDroppedOnLogout() = runTest(dispatcher) {
        val store = SessionStore().apply { current = Session("AT", "RT", 0, userId = "u1") }
        val repo = FakeRepo(store)
        val vm = vm(store, repo)
        runCurrent()
        val pending = repo.posts.last()

        store.current = null // 로그아웃
        runCurrent()
        pending.complete(listOf(item("u1-post")))
        runCurrent()

        assertNull(vm.state.value.myUserId)
        assertEquals(MockDataSource.myProfileFeeds, vm.state.value.feeds) // 이전 계정 글이 아니라 둘러보기 목업
    }

    @Test
    fun switchingAccountShowsOnlyNewAccountsPosts() = runTest(dispatcher) {
        val store = SessionStore().apply { current = Session("AT", "RT", 0, userId = "u1") }
        val repo = FakeRepo(store)
        val vm = vm(store, repo)
        runCurrent()
        val u1Posts = repo.posts.last()

        store.current = Session("AT2", "RT2", 0, userId = "u2")
        runCurrent()
        repo.posts.last().complete(listOf(item("u2-post")))
        u1Posts.complete(listOf(item("u1-post"))) // 늦게 도착
        runCurrent()

        assertEquals("u2", vm.state.value.myUserId)
        assertEquals(listOf("u2-post"), vm.state.value.feeds.map { it.id })
    }
}
