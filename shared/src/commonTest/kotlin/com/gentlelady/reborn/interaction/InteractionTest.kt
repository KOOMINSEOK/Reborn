package com.gentlelady.reborn.interaction

import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.interaction.data.InteractionRepository
import com.gentlelady.reborn.interaction.domain.AddCommentUseCase
import com.gentlelady.reborn.interaction.domain.Comment
import com.gentlelady.reborn.interaction.domain.DeleteCommentUseCase
import com.gentlelady.reborn.interaction.domain.GetCommentsUseCase
import com.gentlelady.reborn.interaction.domain.SetLikeUseCase
import com.gentlelady.reborn.interaction.presentation.CommentCountChange
import com.gentlelady.reborn.interaction.presentation.CommentsIntent
import com.gentlelady.reborn.interaction.presentation.CommentsViewModel
import com.gentlelady.reborn.interaction.presentation.LikeSync
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class InteractionTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    private fun test(block: suspend TestScope.() -> Unit) = runTest(dispatcher) { block() }

    /** 서버 흉내: 요청마다 답을 직접 풀어준다. */
    private class FakeRepo : InteractionRepository {
        val likeCalls = mutableListOf<Boolean>()
        val likeReplies = mutableListOf<CompletableDeferred<Pair<Boolean, Int>>>()
        val pageReplies = mutableListOf<CompletableDeferred<Pair<List<Comment>, Int?>>>()
        val pageOffsets = mutableListOf<Int>()
        val addReplies = mutableListOf<CompletableDeferred<Comment>>()
        val deleteReplies = mutableListOf<CompletableDeferred<Unit>>()

        override suspend fun setLike(target: InteractionTarget, id: String, liked: Boolean): Pair<Boolean, Int> {
            likeCalls += liked
            return CompletableDeferred<Pair<Boolean, Int>>().also { likeReplies += it }.await()
        }
        override suspend fun comments(target: InteractionTarget, id: String, offset: Int): Pair<List<Comment>, Int?> {
            pageOffsets += offset
            return CompletableDeferred<Pair<List<Comment>, Int?>>().also { pageReplies += it }.await()
        }
        override suspend fun addComment(target: InteractionTarget, id: String, body: String): Comment =
            CompletableDeferred<Comment>().also { addReplies += it }.await()
        override suspend fun deleteComment(target: InteractionTarget, commentId: String) =
            CompletableDeferred<Unit>().also { deleteReplies += it }.await()
    }

    private fun comment(id: String) = Comment(id, "u", "a", "b", "2026-10-21", isMine = true)

    private fun commentsVm(repo: FakeRepo) =
        CommentsViewModel(GetCommentsUseCase(repo), AddCommentUseCase(repo), DeleteCommentUseCase(repo))

    // ---------- 좋아요 ----------

    @Test
    fun likeIsOptimisticThenServerValue() = test {
        val repo = FakeRepo()
        val sync = LikeSync(SetLikeUseCase(repo))
        var shown = false to 5
        sync.toggle(this, InteractionTarget.POST, "p", liked = false, likeCount = 5) { l, c -> shown = l to c }
        assertEquals(true to 6, shown) // 바로 반영

        runCurrent()
        repo.likeReplies[0].complete(true to 9) // 그 사이 다른 사람도 눌렀다
        runCurrent()
        assertEquals(true to 9, shown)
    }

    @Test
    fun rapidTapsSendInOrderAndOnlyLastReplyApplies() = test {
        val repo = FakeRepo()
        val sync = LikeSync(SetLikeUseCase(repo))
        var shown = false to 0
        sync.toggle(this, InteractionTarget.POST, "p", liked = false, likeCount = 0) { l, c -> shown = l to c }
        sync.toggle(this, InteractionTarget.POST, "p", liked = true, likeCount = 1) { l, c -> shown = l to c }
        assertEquals(false to 0, shown)

        runCurrent()
        assertEquals(listOf(true), repo.likeCalls) // 두 번째는 첫 번째가 끝날 때까지 기다린다
        repo.likeReplies[0].complete(true to 1)
        runCurrent()
        assertEquals(false to 0, shown) // 중간 응답은 무시
        assertEquals(listOf(true, false), repo.likeCalls)
        repo.likeReplies[1].complete(false to 0)
        runCurrent()
        assertEquals(false to 0, shown)
    }

    @Test
    fun failedLikeRevertsToValueBeforeTap() = test {
        val repo = FakeRepo()
        val sync = LikeSync(SetLikeUseCase(repo))
        var shown = true to 3
        sync.toggle(this, InteractionTarget.POST, "p", liked = true, likeCount = 3) { l, c -> shown = l to c }
        assertEquals(false to 2, shown)

        runCurrent()
        repo.likeReplies[0].completeExceptionally(IllegalStateException("401"))
        runCurrent()
        assertEquals(true to 3, shown)
    }

    @Test
    fun allRapidTapsFailingRevertsToLastServerValueNotIntermediate() = test {
        val repo = FakeRepo()
        val sync = LikeSync(SetLikeUseCase(repo))
        var shown = false to 4
        sync.toggle(this, InteractionTarget.POST, "p", liked = false, likeCount = 4) { l, c -> shown = l to c }
        sync.toggle(this, InteractionTarget.POST, "p", liked = true, likeCount = 5) { l, c -> shown = l to c }

        runCurrent()
        repo.likeReplies[0].completeExceptionally(IllegalStateException("down"))
        runCurrent()
        repo.likeReplies[1].completeExceptionally(IllegalStateException("down"))
        runCurrent()
        assertEquals(false to 4, shown) // 탭 직전(=첫 탭의 낙관값 true/5)이 아니라 서버 값
    }

    @Test
    fun lastTapFailingAfterEarlierSuccessRevertsToThatSuccess() = test {
        val repo = FakeRepo()
        val sync = LikeSync(SetLikeUseCase(repo))
        var shown = false to 4
        sync.toggle(this, InteractionTarget.POST, "p", liked = false, likeCount = 4) { l, c -> shown = l to c }
        sync.toggle(this, InteractionTarget.POST, "p", liked = true, likeCount = 5) { l, c -> shown = l to c }

        runCurrent()
        repo.likeReplies[0].complete(true to 5)
        runCurrent()
        repo.likeReplies[1].completeExceptionally(IllegalStateException("down"))
        runCurrent()
        assertEquals(true to 5, shown) // 서버는 좋아요 상태로 남아 있다
    }

    // ---------- 댓글 ----------

    @Test
    fun failedDeleteDoesNotReenableSendWhileAddInFlight() = test {
        val repo = FakeRepo()
        val vm = commentsVm(repo)
        vm.onIntent(CommentsIntent.Open(InteractionTarget.POST, "p"))
        runCurrent()
        repo.pageReplies[0].complete(listOf(comment("1")) to null)
        runCurrent()

        vm.onIntent(CommentsIntent.UpdateInput("hi"))
        vm.onIntent(CommentsIntent.Send)
        vm.onIntent(CommentsIntent.Delete("1"))
        runCurrent()
        repo.deleteReplies[0].completeExceptionally(IllegalStateException("x"))
        runCurrent()

        assertTrue(vm.state.value.isSending)
        vm.onIntent(CommentsIntent.Send) // 두 번째 전송은 막힌다
        runCurrent()
        assertEquals(1, repo.addReplies.size)
    }

    @Test
    fun deleteDuringLoadMoreDropsStalePageAndReloadsFromShiftedOffset() = test {
        val repo = FakeRepo()
        val vm = commentsVm(repo)
        vm.onIntent(CommentsIntent.Open(InteractionTarget.POST, "p"))
        runCurrent()
        repo.pageReplies[0].complete(listOf(comment("1"), comment("2")) to 2)
        runCurrent()

        vm.onIntent(CommentsIntent.LoadMore) // offset 2 로 받는 중
        vm.onIntent(CommentsIntent.Delete("1"))
        runCurrent()
        repo.deleteReplies[0].complete(Unit) // 삭제가 먼저 끝남 → 서버 목록이 하나 당겨짐
        runCurrent()
        repo.pageReplies[1].complete(listOf(comment("4")) to null) // 옛 offset 기준 응답 (3 을 건너뜀)
        runCurrent()
        assertEquals(listOf("2"), vm.state.value.comments.map { it.id }) // 버렸다

        vm.onIntent(CommentsIntent.LoadMore)
        runCurrent()
        assertEquals(1, repo.pageOffsets.last()) // 당겨진 offset 으로 다시
        repo.pageReplies[2].complete(listOf(comment("3"), comment("4")) to null)
        runCurrent()
        assertEquals(listOf("2", "3", "4"), vm.state.value.comments.map { it.id })
    }

    @Test
    fun countChangesArriveEvenAfterSwitchingToAnotherPost() = test {
        val repo = FakeRepo()
        val vm = commentsVm(repo)
        val changes = mutableListOf<CommentCountChange>()
        val collector = launch { vm.countChanges.collect { changes += it } }
        vm.onIntent(CommentsIntent.Open(InteractionTarget.POST, "p"))
        runCurrent()
        repo.pageReplies[0].complete(emptyList<Comment>() to null)
        runCurrent()
        vm.onIntent(CommentsIntent.UpdateInput("hi"))
        vm.onIntent(CommentsIntent.Send)
        runCurrent()

        vm.onIntent(CommentsIntent.Open(InteractionTarget.POST, "q")) // 시트를 닫고 다른 글을 염
        repo.addReplies[0].complete(comment("9"))
        runCurrent()

        assertEquals(listOf(CommentCountChange(InteractionTarget.POST, "p", +1)), changes)
        assertTrue(vm.state.value.comments.isEmpty()) // 새 글 목록엔 섞이지 않음
        collector.cancel()
    }
}
