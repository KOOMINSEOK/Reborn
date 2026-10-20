package com.gentlelady.reborn.interaction.presentation

import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.interaction.domain.SetLikeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 좋아요 토글을 화면 ViewModel 들이 같이 쓰는 도우미.
 * - 누르면 바로 반영(낙관), 서버 응답이 오면 서버 값으로.
 * - 요청은 순서대로 보낸다 → 빠르게 여러 번 눌러도 서버 최종 상태 = 마지막으로 누른 상태.
 * - 응답은 그 대상의 마지막 탭 것만 반영 (중간 응답이 화면을 깜빡이게 하지 않게).
 * - 실패하면 이번 탭 전 값으로 되돌린다 (로그인 안 함·서버 꺼짐·목업 글).
 *
 * ponytail: 요청 직렬화는 ViewModel 하나에 Mutex 하나 — 대상이 달라도 줄을 선다. 느려지면 대상별 Mutex.
 * 메인 스레드에서만 호출한다 (versions 맵은 동기화하지 않음).
 */
class LikeSync(private val setLike: SetLikeUseCase) {
    private val mutex = Mutex()
    private val versions = mutableMapOf<String, Int>()

    /** [apply] (liked, likeCount) 로 화면 상태를 바꾼다 — 리듀서로 넘기면 된다. */
    fun toggle(
        scope: CoroutineScope,
        target: InteractionTarget,
        id: String,
        liked: Boolean,
        likeCount: Int,
        apply: (Boolean, Int) -> Unit,
    ) {
        val want = !liked
        val version = (versions[id] ?: 0) + 1
        versions[id] = version
        apply(want, (likeCount + if (want) 1 else -1).coerceAtLeast(0))
        scope.launch {
            val result = runCatching { mutex.withLock { setLike(target, id, want) } }
            if (versions[id] != version) return@launch // 그 사이 또 눌렀다 → 그 탭이 마무리한다
            result
                .onSuccess { (serverLiked, serverCount) -> apply(serverLiked, serverCount) }
                .onFailure { apply(liked, likeCount) }
        }
    }
}
