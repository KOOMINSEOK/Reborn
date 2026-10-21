package com.gentlelady.reborn.home

import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.home.domain.repository.HomeRepository
import com.gentlelady.reborn.home.domain.usecase.GetHomeFeedUseCase
import com.gentlelady.reborn.home.presentation.home.HomeIntent
import com.gentlelady.reborn.home.presentation.home.HomeViewModel
import com.gentlelady.reborn.interaction.data.InteractionRepository
import com.gentlelady.reborn.interaction.domain.SetLikeUseCase
import com.gentlelady.reborn.interaction.presentation.LikeSync
import com.gentlelady.reborn.notification.GetUnreadNotificationCountUseCase
import com.gentlelady.reborn.notification.NotificationRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    /** 요청마다 응답을 직접 풀어준다 */
    private class FakeRepo : HomeRepository {
        val replies = mutableListOf<CompletableDeferred<List<HomePost>>>()
        override suspend fun getHomeFeed() = CompletableDeferred<List<HomePost>>().also { replies += it }.await()
    }

    private object NoLikes : InteractionRepository {
        override suspend fun setLike(target: InteractionTarget, id: String, liked: Boolean) = error("unused")
        override suspend fun comments(target: InteractionTarget, id: String, offset: Int) = error("unused")
        override suspend fun addComment(target: InteractionTarget, id: String, body: String) = error("unused")
        override suspend fun deleteComment(target: InteractionTarget, commentId: String) = error("unused")
    }

    private object NoNotifications : NotificationRepository {
        override suspend fun notifications(cursor: String?) = error("unused")
        override suspend fun unreadCount() = 0
        override suspend fun markRead(upTo: String) = Unit
    }

    private fun post(id: String, author: String) = HomePost(id = id, authorId = author, authorName = author, caption = "", postedAt = "")

    @Test
    fun blockedAuthorDisappearsAtOnceAndStaleFeedCannotBringItBack() = runTest(dispatcher) {
        val repo = FakeRepo()
        val vm = HomeViewModel(GetHomeFeedUseCase(repo), LikeSync(SetLikeUseCase(NoLikes)), GetUnreadNotificationCountUseCase(NoNotifications))
        runCurrent()
        repo.replies[0].complete(listOf(post("1", "a"), post("2", "b")))
        runCurrent()

        vm.handleIntent(HomeIntent.LoadFeed) // 차단 전에 출발한 요청 (늦게 온다)
        runCurrent()
        vm.handleIntent(HomeIntent.HideAuthor("a")) // 차단 직후
        vm.handleIntent(HomeIntent.LoadFeed)
        runCurrent()
        assertEquals(listOf("2"), vm.state.value.posts.map { it.id }) // 다시 받기 전에도 안 보인다

        repo.replies[2].complete(listOf(post("2", "b")))
        runCurrent()
        repo.replies[1].complete(listOf(post("1", "a"), post("2", "b"))) // 옛 응답이 늦게 도착
        runCurrent()
        assertEquals(listOf("2"), vm.state.value.posts.map { it.id })
    }
}
