package com.gentlelady.reborn.server.internal

import com.gentlelady.reborn.server.plugins.Db
import com.gentlelady.reborn.server.upload.Storage
import kotlinx.serialization.Serializable

@Serializable
data class CleanupUploadsResponse(val deleted: Int)

/**
 * 고아 사진 정리: 올라갔지만 어떤 글·추억 글·아바타·추모 페이지에도 쓰이지 않는 `images` 버킷 객체를 지운다.
 * 업로드 직후 글 작성 전 사진을 지우면 안 되므로 [GRACE_HOURS] 지난 것만 대상.
 * Cloud Scheduler 가 하루 한 번 POST /internal/cleanup-uploads 를 호출한다.
 */
object UploadCleanupService {

    fun cleanup(): Int {
        if (!Storage.configured) return 0
        val prefix = Storage.publicUrl("")
        val orphans = Db.query(
            """
            select o.name from storage.objects o
            where o.bucket_id = ?
              and o.created_at < now() - make_interval(hours => ?)
              and not exists (select 1 from posts p where ? || o.name = any(p.image_urls))
              and not exists (select 1 from memorial_history h where ? || o.name = any(h.image_urls))
              and not exists (select 1 from profiles pr where pr.avatar_url = ? || o.name)
              and not exists (select 1 from memorials m where m.profile_image_url = ? || o.name)
            limit ?
            """.trimIndent(),
            Storage.BUCKET, GRACE_HOURS, prefix, prefix, prefix, prefix, BATCH,
        ) { it.getString("name") }
        if (orphans.isNotEmpty()) Storage.delete(orphans)
        return orphans.size
    }
}

private const val GRACE_HOURS = 24
// ponytail: 한 번에 최대 1000개. 그보다 많이 쌓이면 다음 호출이 이어서 지운다.
private const val BATCH = 1000
