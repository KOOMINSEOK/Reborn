package com.gentlelady.reborn.server.search

import com.gentlelady.reborn.server.memorial.MEMORIAL_VISIBLE
import com.gentlelady.reborn.server.plugins.Db
import kotlinx.serialization.Serializable
import java.sql.ResultSet
import java.util.UUID

/** 검색 결과 한 줄 — 사람(프로필) 또는 추모 페이지 */
@Serializable
data class SearchResultResponse(
    /** user · memorial */
    val kind: String,
    val id: String,
    val name: String,
    val handle: String,
    val imageUrl: String? = null,
    /** 좋아요순: 사람 = 그 사람 게시물 좋아요 합, 추모 페이지 = 추억 글 좋아요 합 */
    val likes: Long,
    val views: Long,
    /** 받은 조화 수 (추모 페이지만) */
    val flowers: Long,
)

@Serializable
data class SearchResponse(val items: List<SearchResultResponse>)

enum class SearchSort(val column: String) { LIKES("likes"), VIEWS("views"), FLOWERS("flowers") }

class SearchRepository {

    /**
     * 이름·아이디로 사람과 추모 페이지를 함께 찾는다. 가입을 마친 사람, 내가 볼 수 있는 추모 페이지만, 나·차단 관계는 빼고.
     * [sort] 기준 많은 순 → 같으면 좋아요 → 이름.
     */
    fun search(viewerId: UUID, q: String, sort: SearchSort, limit: Int): List<SearchResultResponse> {
        val like = likeEscape(q)
        return Db.query(
            """
            select * from (
                select 'user' as kind, pr.id, pr.display_name as name, pr.handle, pr.avatar_url as image_url,
                       -- 비공개 계정은 팔로우한 사람에게만 (글을 못 보는 사람이 좋아요 합으로 활동을 엿보지 않게) — 아니면 0
                       case when pr.is_private and not exists(select 1 from follows f where f.follower_id = ? and f.followee_id = pr.id)
                            then 0
                            else coalesce((select sum(p.like_count) from posts p where p.author_id = pr.id and p.status = 'published'), 0)
                       end as likes,
                       pr.view_count as views, 0::bigint as flowers
                from profiles pr
                where pr.onboarded_at is not null and pr.id <> ?
                  and (pr.handle ilike ? or pr.display_name ilike ?)
                  and not exists (
                      select 1 from blocks b
                      where (b.blocker_id = pr.id and b.blocked_id = ?) or (b.blocker_id = ? and b.blocked_id = pr.id)
                  )
                union all
                select 'memorial', m.id, m.name, m.handle, m.profile_image_url,
                       coalesce((select sum(h.like_count) from memorial_history h where h.memorial_id = m.id), 0),
                       m.view_count,
                       (select count(*) from wreaths w where w.memorial_id = m.id)
                from memorials m
                where (m.name ilike ? or m.handle ilike ?)
            """.trimIndent() + MEMORIAL_VISIBLE + """
            ) r
            order by ${sort.column} desc, likes desc, name, id
            limit ?
            """.trimIndent(),
            viewerId, viewerId, "$like%", "%$like%", viewerId, viewerId,
            "%$like%", "$like%", viewerId, viewerId, viewerId, viewerId,
            limit,
            map = ResultSet::toResult,
        )
    }

    /**
     * 다른 사람이 프로필·추모 페이지를 열었다 → 조회수 +1 (같은 사람은 하루 한 번).
     * 한 문장: 오늘 기록이 새로 생겼을 때만 올린다.
     */
    fun countView(viewerId: UUID, kind: String, targetId: UUID) {
        val table = when (kind) {
            "profile" -> "profiles"
            "memorial" -> "memorials"
            else -> error("unknown kind")
        }
        Db.update(
            """
            with v as (
                insert into page_views (viewer_id, target_kind, target_id) values (?, ?, ?)
                on conflict do nothing
                returning 1
            )
            update $table set view_count = view_count + 1 where id = ? and exists (select 1 from v)
            """.trimIndent(),
            viewerId, kind, targetId, targetId,
        )
    }
}

/** ilike 에서 % _ \ 를 글자 그대로 */
private fun likeEscape(q: String) = q.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")

private fun ResultSet.toResult() = SearchResultResponse(
    kind = getString("kind"),
    id = getString("id"),
    name = getString("name"),
    handle = getString("handle"),
    imageUrl = getString("image_url"),
    likes = getLong("likes"),
    views = getLong("views"),
    flowers = getLong("flowers"),
)
