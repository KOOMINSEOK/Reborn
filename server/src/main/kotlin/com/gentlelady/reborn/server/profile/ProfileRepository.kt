package com.gentlelady.reborn.server.profile

import com.gentlelady.reborn.server.plugins.Db
import java.sql.ResultSet
import java.util.UUID

/** 프로필 조회/수정 + 팔로워/팔로잉 목록. 카운트는 조회 시 count(*) — 비정규화는 병목일 때. */
class ProfileRepository {

    fun handleTaken(handle: String, excludingUserId: UUID): Boolean =
        Db.queryFirst("select 1 from profiles where handle = ? and id <> ?", handle, excludingUserId) { true } ?: false

    fun getMy(userId: UUID): MyProfileResponse? =
        Db.queryFirst(MY_SELECT + " where p.id = ?", userId) { it.toMyProfile() }

    fun updateMy(userId: UUID, req: UpdateProfileRequest): MyProfileResponse? {
        val sets = mutableListOf<String>()
        val params = mutableListOf<Any?>()
        req.displayName?.let { sets += "display_name = ?"; params += it }
        req.handle?.let { sets += "handle = ?"; params += it }
        req.bio?.let { sets += "bio = ?"; params += it }
        req.avatarUrl?.let { sets += "avatar_url = ?"; params += it }
        req.isPrivate?.let { sets += "is_private = ?"; params += it }

        if (sets.isNotEmpty()) {
            params += userId
            @Suppress("SpreadOperator") // 동적 SET 절 — 컬럼명은 코드 상수, 값만 바인딩.
            Db.update("update profiles set ${sets.joinToString(", ")} where id = ?", *params.toTypedArray())
        }
        return getMy(userId)
    }

    fun getPublic(viewerId: UUID, targetId: UUID): PublicProfileResponse? =
        Db.queryFirst(
            """
            select p.id, p.handle, p.display_name, p.bio, p.avatar_url, p.is_private,
                   (select count(*) from follows where followee_id = p.id) as follower_count,
                   (select count(*) from follows where follower_id = p.id) as following_count,
                   exists(select 1 from follows where follower_id = ? and followee_id = p.id) as followed_by_me,
                   exists(select 1 from blocks where blocker_id = ? and blocked_id = p.id) as blocked_by_me
            from profiles p where p.id = ?
            """.trimIndent(),
            viewerId, viewerId, targetId,
        ) {
            PublicProfileResponse(
                id = it.getString("id"),
                handle = it.getString("handle"),
                displayName = it.getString("display_name"),
                bio = it.getString("bio"),
                avatarUrl = it.getString("avatar_url"),
                isPrivate = it.getBoolean("is_private"),
                followerCount = it.getInt("follower_count"),
                followingCount = it.getInt("following_count"),
                followedByMe = it.getBoolean("followed_by_me"),
                blockedByMe = it.getBoolean("blocked_by_me"),
            )
        }

    fun followers(viewerId: UUID, targetId: UUID, page: Page): List<UserSummaryResponse> =
        Db.query(
            USER_SUMMARY_SELECT + """
            from follows f join profiles pr on pr.id = f.follower_id
            where f.followee_id = ?
            order by f.created_at desc limit ? offset ?
            """.trimIndent(),
            viewerId, targetId, page.limit, page.offset,
            map = ResultSet::toUserSummary,
        )

    fun following(viewerId: UUID, targetId: UUID, page: Page): List<UserSummaryResponse> =
        Db.query(
            USER_SUMMARY_SELECT + """
            from follows f join profiles pr on pr.id = f.followee_id
            where f.follower_id = ?
            order by f.created_at desc limit ? offset ?
            """.trimIndent(),
            viewerId, targetId, page.limit, page.offset,
            map = ResultSet::toUserSummary,
        )
}

data class Page(val offset: Int, val limit: Int)

private const val MY_SELECT = """
    select p.id, p.handle, p.display_name, p.bio, p.avatar_url, p.is_private,
           (select count(*) from follows where followee_id = p.id) as follower_count,
           (select count(*) from follows where follower_id = p.id) as following_count
    from profiles p
"""

/** 첫 `?` = viewerId (followed_by_me). */
private const val USER_SUMMARY_SELECT = """
    select pr.id, pr.handle, pr.display_name, pr.avatar_url,
           exists(select 1 from follows f2 where f2.follower_id = ? and f2.followee_id = pr.id) as followed_by_me
"""

private fun ResultSet.toMyProfile() = MyProfileResponse(
    id = getString("id"),
    handle = getString("handle"),
    displayName = getString("display_name"),
    bio = getString("bio"),
    avatarUrl = getString("avatar_url"),
    isPrivate = getBoolean("is_private"),
    followerCount = getInt("follower_count"),
    followingCount = getInt("following_count"),
)

private fun ResultSet.toUserSummary() = UserSummaryResponse(
    id = getString("id"),
    handle = getString("handle"),
    displayName = getString("display_name"),
    avatarUrl = getString("avatar_url"),
    followedByMe = getBoolean("followed_by_me"),
)
