package com.gentlelady.reborn.interaction

import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.interaction.data.InteractionRepository
import com.gentlelady.reborn.interaction.domain.Comment
import com.gentlelady.reborn.interaction.domain.SetLikeUseCase
import com.gentlelady.reborn.interaction.presentation.CommentsReducer
import com.gentlelady.reborn.interaction.presentation.CommentsResult
import com.gentlelady.reborn.interaction.presentation.CommentsState
import com.gentlelady.reborn.interaction.presentation.LikeSync
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class InteractionTest {

    /** 서버 흉내: 요청마다 답을 직접 풀어준다. */
    private class FakeRepo : InteractionRepository {
        val calls = mutableListOf<Boolean>()
        val replies = mutableListOf<CompletableDeferred<Pair<Boolean, Int>>>()
        override suspend fun setLike(target: InteractionTarget, id: String, liked: Boolean): Pair<Boolean, Int> {
            calls += liked
            return CompletableDeferred<Pair<Boolean, Int>>().also { replies += it }.await()
        }
        override suspend fun comments(target: InteractionTarget, id: String, offset: Int) = error("unused")
        override suspend fun addComment(target: InteractionTarget, id: String, body: String) = error("unused")
        override suspend fun deleteComment(target: InteractionTarget, commentId: String) = error("unused")
    }

    @Test
    fun likeIsOptimisticThenServerValue() = runTest {
        val repo = FakeRepo()
        val sync = LikeSync(SetLikeUseCase(repo))
        var shown = false to 5
        sync.toggle(this, InteractionTarget.POST, "p", liked = false, likeCount = 5) { l, c -> shown = l to c }
        assertEquals(true to 6, shown) // 바로 반영

        runCurrent()
        repo.replies[0].complete(true to 9) // 그 사이 다른 사람도 눌렀다
        runCurrent()
        assertEquals(true to 9, shown)
    }

    @Test
    fun rapidTapsSendInOrderAndOnlyLastReplyApplies() = runTest {
        val repo = FakeRepo()
        val sync = LikeSync(SetLikeUseCase(repo))
        var shown = false to 0
        sync.toggle(this, InteractionTarget.POST, "p", liked = false, likeCount = 0) { l, c -> shown = l to c }
        sync.toggle(this, InteractionTarget.POST, "p", liked = true, likeCount = 1) { l, c -> shown = l to c }
        assertEquals(false to 0, shown)

        runCurrent()
        assertEquals(listOf(true), repo.calls) // 두 번째는 첫 번째가 끝날 때까지 기다린다
        repo.replies[0].complete(true to 1)
        runCurrent()
        assertEquals(false to 0, shown) // 중간 응답은 무시
        assertEquals(listOf(true, false), repo.calls)
        repo.replies[1].complete(false to 0)
        runCurrent()
        assertEquals(false to 0, shown)
    }

    @Test
    fun failedLikeRevertsToValueBeforeTap() = runTest {
        val repo = FakeRepo()
        val sync = LikeSync(SetLikeUseCase(repo))
        var shown = true to 3
        sync.toggle(this, InteractionTarget.POST, "p", liked = true, likeCount = 3) { l, c -> shown = l to c }
        assertEquals(false to 2, shown)

        runCurrent()
        repo.replies[0].completeExceptionally(IllegalStateException("401"))
        runCurrent()
        assertEquals(true to 3, shown)
    }

    private fun comment(id: String) = Comment(id, "a", "b", "2026-10-21", isMine = true)

    @Test
    fun commentCountDeltaAndOffsetsStayConsistent() {
        var s = CommentsState(parentId = "p")
        s = CommentsReducer.reduce(s, CommentsResult.PageLoaded(listOf(comment("1"), comment("2")), nextOffset = 2, append = false))
        s = CommentsReducer.reduce(s, CommentsResult.Sent(comment("3")))
        assertEquals(listOf("1", "2"), s.comments.map { it.id }) // 아직 다 안 받았으니 스크롤로 받을 때 붙는다
        s = CommentsReducer.reduce(s, CommentsResult.Deleted("1"))
        assertEquals(1, s.nextOffset) // 앞에서 하나 빠졌으니 다음 페이지가 하나 당겨진다
        s = CommentsReducer.reduce(s, CommentsResult.PageLoaded(listOf(comment("2"), comment("3")), nextOffset = null, append = true))
        assertEquals(listOf("2", "3"), s.comments.map { it.id }) // 중복 없음
        assertEquals(0, s.countDelta) // +1 -1
    }
}
