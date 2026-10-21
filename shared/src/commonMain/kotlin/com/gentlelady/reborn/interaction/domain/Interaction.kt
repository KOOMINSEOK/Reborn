package com.gentlelady.reborn.interaction.domain

import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.interaction.data.InteractionRepository

data class Comment(
    val id: String,
    /** 누르면 그 사람 프로필로 */
    val authorId: String,
    val authorName: String,
    val body: String,
    val date: String,
    /** 내가 쓴 댓글이면 삭제 가능 */
    val isMine: Boolean,
)

class SetLikeUseCase(private val repository: InteractionRepository) {
    suspend operator fun invoke(target: InteractionTarget, id: String, liked: Boolean) =
        repository.setLike(target, id, liked)
}

class GetCommentsUseCase(private val repository: InteractionRepository) {
    suspend operator fun invoke(target: InteractionTarget, id: String, offset: Int) =
        repository.comments(target, id, offset)
}

class AddCommentUseCase(private val repository: InteractionRepository) {
    suspend operator fun invoke(target: InteractionTarget, id: String, body: String) =
        repository.addComment(target, id, body)
}

class DeleteCommentUseCase(private val repository: InteractionRepository) {
    suspend operator fun invoke(target: InteractionTarget, commentId: String) =
        repository.deleteComment(target, commentId)
}
