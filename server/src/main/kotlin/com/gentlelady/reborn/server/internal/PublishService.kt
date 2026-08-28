package com.gentlelady.reborn.server.internal

import com.gentlelady.reborn.server.plugins.Db
import kotlinx.serialization.Serializable

@Serializable
data class PublishDueResponse(val published: Int)

/**
 * 예약(생후) 게시물 발행: publish_at 이 지난 scheduled 글을 published 로 뒤집는다.
 * created_at 을 발행 시점으로 갱신 → 피드에 새 글로 뜬다.
 */
object PublishService {
    fun publishDue(): Int = Db.update(
        """
        update posts
        set status = 'published', created_at = now()
        where status = 'scheduled' and publish_at is not null and publish_at <= now()
        """.trimIndent(),
    )
}
