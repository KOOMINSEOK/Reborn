package com.gentlelady.reborn.interaction.data

import com.gentlelady.reborn.core.auth.SessionStore
import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.CommentDto
import com.gentlelady.reborn.interaction.domain.Comment

/** 좋아요·댓글. 쓰기·읽기 모두 mock 폴백 없음 — 실패 시 예외 (화면이 되돌리거나 오류를 보여준다). */
interface InteractionRepository {
    /** 서버 기준 (liked, likeCount). */
    suspend fun setLike(target: InteractionTarget, id: String, liked: Boolean): Pair<Boolean, Int>

    /** 오래된 순 한 페이지 + 다음 offset (null 이면 끝). */
    suspend fun comments(target: InteractionTarget, id: String, offset: Int): Pair<List<Comment>, Int?>

    suspend fun addComment(target: InteractionTarget, id: String, body: String): Comment

    suspend fun deleteComment(target: InteractionTarget, commentId: String)
}

class InteractionRepositoryImpl(
    private val api: RebornApi,
    private val session: SessionStore,
) : InteractionRepository {

    override suspend fun setLike(target: InteractionTarget, id: String, liked: Boolean): Pair<Boolean, Int> =
        api.setLike(target, id, liked).let { it.liked to it.likeCount }

    override suspend fun comments(target: InteractionTarget, id: String, offset: Int): Pair<List<Comment>, Int?> =
        api.comments(target, id, offset).let { page -> page.items.map { it.toComment() } to page.nextOffset }

    override suspend fun addComment(target: InteractionTarget, id: String, body: String): Comment =
        api.addComment(target, id, body).toComment()

    override suspend fun deleteComment(target: InteractionTarget, commentId: String) =
        api.deleteComment(target, commentId)

    private fun CommentDto.toComment() = Comment(
        id = id,
        authorName = authorName,
        body = body,
        date = createdAt.take(10),
        isMine = authorId == session.current?.userId,
    )
}
