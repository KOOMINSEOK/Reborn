package com.gentlelady.reborn.server.seed

import com.gentlelady.reborn.server.Env
import com.gentlelady.reborn.server.plugins.Db
import io.ktor.util.logging.Logger
import java.time.OffsetDateTime
import java.util.UUID

private const val IMG_PREFIX = "/static/seed/"

/**
 * SEED_DEV=true 일 때 시작 시 실행. 앱 mock 데이터를 서버 DB 에 넣는다.
 * - 버전([SeedData.VERSION])마다 한 번만 돈다. 끝나면 seed_runs 에 기록 → 다음 기동은 건너뜀.
 *   (사람이 지운 시드 좋아요·팔로우·댓글이 재시작 때마다 되살아나지 않게. 시드를 고치면 버전을 올린다)
 * - 고정 UUID + on conflict → 같은 버전을 다시 돌려도 안전. 실서비스에선 플래그만 끄면 됨.
 * - best-effort: 실패해도 서버 기동을 막지 않는다 (기록을 안 남기니 다음 기동에 다시 시도).
 */
object SeedRunner {

    fun runIfEnabled(log: Logger) {
        if (Env.get("SEED_DEV")?.lowercase() != "true") return
        if (Db.dataSource == null) {
            log.warn("SEED_DEV=true 이지만 DB 미연결 — 시드 건너뜀")
            return
        }
        runCatching {
            repairSeedAuthUsers()
            // 시드 전용 기록 표. 마이그레이션이 아니라 여기서 만든다 → SEED_DEV 를 켠 DB 에만 생긴다
            Db.update("create table if not exists seed_runs (version text primary key, ran_at timestamptz not null default now())")
            if (Db.queryFirst("select 1 from seed_runs where version = ?", SeedData.VERSION) { true } == true) {
                log.info("시드 ${SeedData.VERSION} 이미 적용됨 — 건너뜀")
                return
            }
            seedUsers()
            seedPosts()
            seedMemorialAndHistory()
            seedGuestbook()
            seedSocial()
            Db.update("insert into seed_runs (version) values (?) on conflict do nothing", SeedData.VERSION)
        }.onFailure { log.error("시드 실패 (서버는 계속 기동)", it) }
            .onSuccess {
                log.info(
                    "시드 ${SeedData.VERSION} 완료: 유저 ${SeedData.users.size}, 게시물 ${SeedData.posts.size}, " +
                        "히스토리 ${SeedData.history.size}, 방명록 ${SeedData.guestbook.size}, " +
                    "팔로우 ${SeedData.follows.size}, 좋아요 ${SeedData.postLikes.size + SeedData.historyLikes.size}, " +
                    "댓글 ${SeedData.postComments.size + SeedData.historyComments.size}",
                )
            }
    }

    /**
     * 예전 시드는 auth.users 의 토큰 칸을 NULL 로 넣었다 → Supabase(GoTrue)가 그 줄을 못 읽어
     * 관리자 사용자 목록·그 계정 세션 만들기가 "Database error finding user" 로 실패했다. 빈 글자로 채운다 (여러 번 해도 같음)
     */
    private fun repairSeedAuthUsers() {
        Db.update(
            """
            update auth.users set
                confirmation_token = coalesce(confirmation_token, ''), recovery_token = coalesce(recovery_token, ''),
                email_change_token_new = coalesce(email_change_token_new, ''), email_change = coalesce(email_change, ''),
                email_change_token_current = coalesce(email_change_token_current, ''),
                phone_change = coalesce(phone_change, ''), phone_change_token = coalesce(phone_change_token, ''),
                reauthentication_token = coalesce(reauthentication_token, '')
            where id::text = any(?) and (confirmation_token is null or recovery_token is null or email_change_token_new is null
                or email_change is null or email_change_token_current is null or phone_change is null
                or phone_change_token is null or reauthentication_token is null)
            """.trimIndent(),
            SeedData.users.map { it.id.toString() }.toTypedArray(),
        )
    }

    private fun seedUsers() {
        SeedData.users.forEach { u ->
            // auth.users 삽입 → V2 트리거가 public.profiles 자동 생성
            Db.update(
                """
                insert into auth.users (instance_id, id, aud, role, email, encrypted_password,
                    email_confirmed_at, created_at, updated_at, raw_app_meta_data, raw_user_meta_data,
                    confirmation_token, recovery_token, email_change_token_new, email_change,
                    email_change_token_current, phone_change, phone_change_token, reauthentication_token)
                values ('00000000-0000-0000-0000-000000000000', ?, 'authenticated', 'authenticated', ?, '',
                    now(), now(), now(), '{"provider":"email","providers":["email"]}'::jsonb, ?::jsonb,
                    '', '', '', '', '', '', '', '')
                on conflict (id) do nothing
                """.trimIndent(),
                u.id, u.email, """{"name":"${u.name}"}""",
            )
            // 트리거가 만든 profiles 행에 bio 채우기 (핸들/이름은 트리거가 이미 설정)
            // 사용자가 앱에서 바꾼 값은 덮지 않는다 (비어 있을 때만 / 시드가 넣은 사진일 때만)
            u.bio?.let { Db.update("update profiles set bio = ? where id = ? and bio is null", it, u.id) }
            // 가입 트리거가 만든 기본 핸들(user_<uuid>) 그대로일 때만, 그 핸들을 아무도 안 쓸 때만
            Db.update(
                """
                update profiles set handle = ?
                where id = ? and handle = 'user_' || replace(id::text, '-', '')
                  and not exists (select 1 from profiles where handle = ?)
                """.trimIndent(),
                u.handle, u.id, u.handle,
            )
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
                    where posts.author_id = excluded.author_id
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
                    where memorial_history.author_id = excluded.author_id
                      and memorial_history.memorial_id = excluded.memorial_id
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
        fun id(name: String) = SeedData.userIdByName.getValue(name)
        SeedData.follows.forEach { (follower, followee) ->
            Db.update(
                """
                insert into follows (follower_id, followee_id)
                select ?::uuid, ?::uuid
                where not exists (
                    select 1 from blocks
                    where (blocker_id = ?::uuid and blocked_id = ?::uuid) or (blocker_id = ?::uuid and blocked_id = ?::uuid)
                )
                on conflict do nothing
                """.trimIndent(),
                id(follower), id(followee), id(follower), id(followee), id(followee), id(follower),
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
