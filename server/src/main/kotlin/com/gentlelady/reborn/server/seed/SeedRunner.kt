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
        }.onFailure { log.error("시드 실패 (서버는 계속 기동)", it) }
            .onSuccess {
                log.info(
                    "시드 완료: 유저 ${SeedData.users.size}, 게시물 ${SeedData.posts.size}, " +
                        "히스토리 ${SeedData.history.size}, 방명록 ${SeedData.guestbook.size}",
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
        }
    }

    private fun seedPosts() {
        SeedData.posts.forEach { p ->
            Db.update(
                """
                insert into posts (id, author_id, caption, image_url, is_posthumous, status, created_at)
                values (?, ?, ?, ?, ?, 'published', ?)
                on conflict (id) do nothing
                """.trimIndent(),
                p.id, SeedData.userIdByName.getValue(p.authorName), p.caption,
                IMG_PREFIX + p.image, p.posthumous, daysAgo(p.daysAgo),
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
        SeedData.history.forEach { h ->
            Db.update(
                """
                insert into memorial_history (id, memorial_id, author_id, caption, image_url, created_at)
                values (?, ?, ?, ?, ?, ?)
                on conflict (id) do nothing
                """.trimIndent(),
                h.id, SeedData.memorialId, SeedData.userIdByName.getValue(h.authorName),
                h.caption, IMG_PREFIX + h.image, daysAgo(h.daysAgo),
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

    private fun daysAgo(n: Long): OffsetDateTime = OffsetDateTime.now().minusDays(n)
}
