package com.gentlelady.reborn.server.seed

import com.gentlelady.reborn.server.Env
import com.gentlelady.reborn.server.plugins.Db
import io.ktor.util.logging.Logger
import java.time.OffsetDateTime
import java.util.UUID

private const val IMG_PREFIX = "/static/seed/"

/**
 * SEED_DEV=true 일 때 시작 시 실행. 앱 mock 데이터를 서버 DB 에 넣는다.
 * 고정 UUID + on conflict do nothing → 몇 번 켜도 안전. 실서비스에선 플래그만 끄면 됨.
 * best-effort: 실패해도 서버 기동을 막지 않는다.
 */
object SeedRunner {

    fun runIfEnabled(log: Logger) {
        if (Env.get("SEED_DEV")?.lowercase() != "true") return
        if (Db.dataSource == null) {
            log.warn("SEED_DEV=true 이지만 DB 미연결 — 시드 건너뜀")
            return
        }
        runCatching {
            seedUsers()
            seedPosts()
            seedMemorialAndHistory()
            seedGuestbook()
            seedSocial()
        }.onFailure { log.error("시드 실패 (서버는 계속 기동)", it) }
            .onSuccess {
                log.info(
                    "시드 완료: 유저 ${SeedData.users.size}, 게시물 ${SeedData.posts.size}, " +
                        "히스토리 ${SeedData.history.size}, 방명록 ${SeedData.guestbook.size}, " +
                    "팔로우 ${SeedData.follows.size}, 좋아요 ${SeedData.postLikes.size + SeedData.historyLikes.size}, " +
                    "댓글 ${SeedData.postComments.size + SeedData.historyComments.size}",
                )
            }
    }

    private fun seedUsers() {
        SeedData.users.forEach { u ->
            // auth.users 삽입 → V2 트리거가 public.profiles 자동 생성
            Db.update(
                """
                insert into auth.users (instance_id, id, aud, role, email, encrypted_password,
                    email_confirmed_at, created_at, updated_at, raw_app_meta_data, raw_user_meta_data)
                values ('00000000-0000-0000-0000-000000000000', ?, 'authenticated', 'authenticated', ?, '',
                    now(), now(), now(), '{"provider":"email","providers":["email"]}'::jsonb, ?::jsonb)
                on conflict (id) do nothing
                """.trimIndent(),
                u.id, u.email, """{"name":"${u.name}"}""",
            )
            // 트리거가 만든 profiles 행에 bio 채우기 (핸들/이름은 트리거가 이미 설정)
            // 사용자가 앱에서 바꾼 값은 덮지 않는다 (비어 있을 때만 / 시드가 넣은 사진일 때만)
            u.bio?.let { Db.update("update profiles set bio = ? where id = ? and bio is null", it, u.id) }
            Db.update(
                "update profiles set avatar_url = ? where id = ? and (avatar_url is null or avatar_url like '$IMG_PREFIX%')",
                u.avatar?.let { IMG_PREFIX + it }, u.id,
            )
        }
    }

    private fun seedPosts() {
        SeedData.posts.forEach { p ->
            Db.update(
                """
                insert into posts (id, author_id, caption, image_urls, is_posthumous, status, created_at)
                values (?, ?, ?, ?, ?, 'published', ?)
                on conflict (id) do update set caption = excluded.caption, image_urls = excluded.image_urls,
                    is_posthumous = excluded.is_posthumous
                """.trimIndent(),
                p.id, SeedData.userIdByName.getValue(p.authorName), p.caption,
                p.images.map { IMG_PREFIX + it }.toTypedArray(), p.posthumous, daysAgo(p.daysAgo),
            )
        }
        SeedData.scheduledPosts.forEach { s ->
            Db.update(
                """
                insert into posts (id, author_id, caption, is_posthumous, status, publish_at)
                values (?, ?, ?, true, 'scheduled', ?)
                on conflict (id) do nothing
                """.trimIndent(),
                s.id, SeedData.userIdByName.getValue(s.authorName), s.caption,
                OffsetDateTime.now().plusMinutes(s.publishInMinutes),
            )
        }
    }

    private fun seedMemorialAndHistory() {
        Db.update(
            """
            insert into memorials (id, creator_id, name, handle, bio, visibility)
            values (?, ?, ?, ?, ?, 'public')
            on conflict (id) do nothing
            """.trimIndent(),
            SeedData.memorialId, SeedData.userIdByName.getValue(SeedData.MEMORIAL_CREATOR),
            SeedData.MEMORIAL_NAME, SeedData.MEMORIAL_HANDLE, SeedData.MEMORIAL_BIO,
        )
        Db.update(
            "update memorials set profile_image_url = ? where id = ? and (profile_image_url is null or profile_image_url like '$IMG_PREFIX%')",
            IMG_PREFIX + SeedData.MEMORIAL_IMAGE, SeedData.memorialId,
        )
        SeedData.history.forEach { h ->
            Db.update(
                """
                insert into memorial_history (id, memorial_id, author_id, caption, image_urls, created_at)
                values (?, ?, ?, ?, ?, ?)
                on conflict (id) do update set caption = excluded.caption, image_urls = excluded.image_urls
                """.trimIndent(),
                h.id, SeedData.memorialId, SeedData.userIdByName.getValue(h.authorName),
                h.caption, arrayOf(IMG_PREFIX + h.image), daysAgo(h.daysAgo),
            )
        }
    }

    private fun seedGuestbook() {
        SeedData.guestbook.forEach { g ->
            Db.update(
                """
                insert into guestbook_entries (id, memorial_id, author_id, message, created_at)
                values (?, ?, ?, ?, ?)
                on conflict (id) do nothing
                """.trimIndent(),
                g.id, SeedData.memorialId, SeedData.userIdByName.getValue(g.authorName), g.message, daysAgo(g.daysAgo),
            )
        }
    }

    /** 팔로우·좋아요·댓글. 카운트는 V3~V5 트리거가 맞춘다 (on conflict 로 건너뛴 행은 트리거도 안 탐 → 중복 집계 없음). */
    private fun seedSocial() {
        // 수백 건이라 원격 DB 면 기동이 수 초 늦어진다 → 마지막 행이 이미 있으면 통째로 건너뜀
        // ponytail: 마커 1건으로 판단 — 시드를 고치면 마지막 댓글 id 도 바뀌므로 다시 돈다
        val marker = SeedData.historyComments.last().id
        if (Db.queryFirst("select 1 from history_comments where id = ?", marker) { true } == true) return
        fun id(name: String) = SeedData.userIdByName.getValue(name)
        SeedData.follows.forEach { (follower, followee) ->
            Db.update(
                "insert into follows (follower_id, followee_id) values (?, ?) on conflict do nothing",
                id(follower), id(followee),
            )
        }
        SeedData.memorialFollowers.forEach {
            Db.update(
                "insert into memorial_follows (follower_id, memorial_id) values (?, ?) on conflict do nothing",
                id(it), SeedData.memorialId,
            )
        }
        SeedData.postLikes.forEach { (postId, user) ->
            Db.update("insert into post_likes (post_id, user_id) values (?, ?) on conflict do nothing", postId, id(user))
        }
        SeedData.historyLikes.forEach { (historyId, user) ->
            Db.update("insert into history_likes (history_id, user_id) values (?, ?) on conflict do nothing", historyId, id(user))
        }
        // 댓글 시각을 1분씩 벌려 순서가 정해지게 (오래된 순 정렬)
        SeedData.postComments.forEachIndexed { k, c ->
            Db.update(
                """
                insert into post_comments (id, post_id, author_id, body, created_at)
                values (?, ?, ?, ?, ?)
                on conflict (id) do nothing
                """.trimIndent(),
                c.id, c.parentId, id(c.authorName), c.body, minutesAgo(SeedData.postComments.size - k),
            )
        }
        SeedData.historyComments.forEachIndexed { k, c ->
            Db.update(
                """
                insert into history_comments (id, history_id, author_id, body, created_at)
                values (?, ?, ?, ?, ?)
                on conflict (id) do nothing
                """.trimIndent(),
                c.id, c.parentId, id(c.authorName), c.body, minutesAgo(SeedData.historyComments.size - k),
            )
        }
    }

    private fun minutesAgo(n: Int): OffsetDateTime = OffsetDateTime.now().minusMinutes(n.toLong())

    private fun daysAgo(n: Long): OffsetDateTime = OffsetDateTime.now().minusDays(n)
}
