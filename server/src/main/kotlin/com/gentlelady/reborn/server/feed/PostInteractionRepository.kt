package com.gentlelady.reborn.server.feed

import com.gentlelady.reborn.server.plugins.Db
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.UUID

/** 게시물 좋아요 / 댓글. 카운트는 DB 트리거가 동기화하므로 여기선 읽기만 한다. */
class PostInteractionRepository {

    fun postExists(postId: UUID): Boolean =
        Db.queryFirst("select 1 from posts where id = ?", postId) { true } ?: false

    fun like(userId: UUID, postId: UUID): LikeResponse {
        Db.update(
            "insert into post_likes (post_id, user_id) values (?, ?) on conflict do nothing",
            postId, userId,
        )
        return likeState(userId, postId)
    }

    fun unlike(userId: UUID, postId: UUID): LikeResponse {
        Db.update("delete from post_likes where post_id = ? and user_id = ?", postId, userId)
        return likeState(userId, postId)
    }

    private fun likeState(userId: UUID, postId: UUID): LikeResponse =
        Db.queryFirst(
            """
            select p.like_count,
                   exists(select 1 from post_likes pl where pl.post_id = p.id and pl.user_id = ?) as liked
            from posts p where p.id = ?
            """.trimIndent(),
            userId, postId,
        ) { LikeResponse(liked = it.getBoolean("liked"), likeCount = it.getInt("like_count")) }
            ?: LikeResponse(liked = false, likeCount = 0)

    fun addComment(authorId: UUID, postId: UUID, body: String): CommentResponse =
        Db.queryFirst(
            """
            with c as (
                insert into post_comments (post_id, author_id, body)
                values (?, ?, ?)
                returning id, post_id, author_id, body, created_at
            )
            select c.id, c.post_id, c.author_id, pr.display_name as author_name, c.body, c.created_at
            from c join profiles pr on pr.id = c.author_id
            """.trimIndent(),
            postId, authorId, body,
            map = ResultSet::toComment,
        ) ?: error("comment insert returned nothing")

    fun listComments(postId: UUID, offset: Int, limit: Int): List<CommentResponse> =
        Db.query(
            """
            select c.id, c.post_id, c.author_id, pr.display_name as author_name, c.body, c.created_at
            from post_comments c
            join profiles pr on pr.id = c.author_id
            where c.post_id = ?
            order by c.created_at asc
            limit ? offset ?
            """.trimIndent(),
            postId, limit, offset,
            map = ResultSet::toComment,
        )

    /** 본인 댓글만 삭제. 삭제된 행 수(0 = 없음 또는 남의 댓글). */
    fun deleteComment(commentId: UUID, requesterId: UUID): Int =
        Db.update("delete from post_comments where id = ? and author_id = ?", commentId, requesterId)
}

private fun ResultSet.toComment() = CommentResponse(
    id = getString("id"),
    postId = getString("post_id"),
    authorId = getString("author_id"),
    authorName = getString("author_name"),
    body = getString("body"),
    createdAt = getObject("created_at", OffsetDateTime::class.java).toString(),
)
