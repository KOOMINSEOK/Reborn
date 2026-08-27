package com.gentlelady.reborn.server.interaction

import com.gentlelady.reborn.server.plugins.Db
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.UUID

/**
 * 좋아요/댓글 공통 로직. posts 와 memorial_history 가 동일 구조라 한 클래스로 재사용한다.
 * 테이블/컬럼 이름은 코드 상수(사용자 입력 아님)라 인젝션 위험 없음.
 * 부모의 like_count/comment_count 는 DB 트리거가 관리 — 여기선 읽기만 한다.
 */
class InteractionRepo(
    private val parentTable: String,
    private val likeTable: String,
    private val commentTable: String,
    private val parentIdCol: String,
) {
    fun parentExists(id: UUID): Boolean =
        Db.queryFirst("select 1 from $parentTable where id = ?", id) { true } ?: false

    fun like(userId: UUID, parentId: UUID): LikeResponse {
        Db.update(
            "insert into $likeTable ($parentIdCol, user_id) values (?, ?) on conflict do nothing",
            parentId, userId,
        )
        return likeState(userId, parentId)
    }

    fun unlike(userId: UUID, parentId: UUID): LikeResponse {
        Db.update("delete from $likeTable where $parentIdCol = ? and user_id = ?", parentId, userId)
        return likeState(userId, parentId)
    }

    private fun likeState(userId: UUID, parentId: UUID): LikeResponse =
        Db.queryFirst(
            """
            select t.like_count,
                   exists(select 1 from $likeTable l where l.$parentIdCol = t.id and l.user_id = ?) as liked
            from $parentTable t where t.id = ?
            """.trimIndent(),
            userId, parentId,
        ) { LikeResponse(liked = it.getBoolean("liked"), likeCount = it.getInt("like_count")) }
            ?: LikeResponse(liked = false, likeCount = 0)

    fun addComment(authorId: UUID, parentId: UUID, body: String): CommentResponse =
        Db.queryFirst(
            """
            with c as (
                insert into $commentTable ($parentIdCol, author_id, body)
                values (?, ?, ?)
                returning id, $parentIdCol as parent_id, author_id, body, created_at
            )
            select c.id, c.parent_id, c.author_id, pr.display_name as author_name, c.body, c.created_at
            from c join profiles pr on pr.id = c.author_id
            """.trimIndent(),
            parentId, authorId, body,
            map = ResultSet::toComment,
        ) ?: error("comment insert returned nothing")

    fun listComments(parentId: UUID, offset: Int, limit: Int): List<CommentResponse> =
        Db.query(
            """
            select c.id, c.$parentIdCol as parent_id, c.author_id, pr.display_name as author_name,
                   c.body, c.created_at
            from $commentTable c
            join profiles pr on pr.id = c.author_id
            where c.$parentIdCol = ?
            order by c.created_at asc
            limit ? offset ?
            """.trimIndent(),
            parentId, limit, offset,
            map = ResultSet::toComment,
        )

    /** 본인 댓글만. 삭제된 행 수(0 = 없음 또는 남의 댓글). */
    fun deleteComment(commentId: UUID, requesterId: UUID): Int =
        Db.update("delete from $commentTable where id = ? and author_id = ?", commentId, requesterId)
}

private fun ResultSet.toComment() = CommentResponse(
    id = getString("id"),
    parentId = getString("parent_id"),
    authorId = getString("author_id"),
    authorName = getString("author_name"),
    body = getString("body"),
    createdAt = getObject("created_at", OffsetDateTime::class.java).toString(),
)
