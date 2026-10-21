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
 * - 요청은 순서대로 보낸다(Mutex 는 FIFO) → 빠르게 여러 번 눌러도 서버 최종 상태 = 마지막으로 누른 상태.
 * - 화면 반영은 그 대상의 마지막 탭이 끝날 때만 (중간 응답이 화면을 깜빡이게 하지 않게).
 * - 마지막 탭이 실패하면 "서버가 마지막으로 확인해 준 값"으로 되돌린다.
 *   (탭 직전 값이 아니라 — 그건 앞 탭의 낙관값일 수 있어 서버에 없던 상태다)
 *
 * ponytail: 요청 직렬화는 ViewModel 하나에 Mutex 하나 — 대상이 달라도 줄을 선다. 느려지면 대상별 Mutex.
 * 메인 스레드에서만 호출한다 (맵들은 동기화하지 않음).
 */
class LikeSync(private val setLike: SetLikeUseCase) {
    private val mutex = Mutex()
    private val versions = mutableMapOf<String, Int>()
    private val pending = mutableMapOf<String, Int>()
    /** 대상별 서버가 확인한 (liked, likeCount). 진행 중인 요청이 있을 때만 들고 있다. */
    private val confirmed = mutableMapOf<String, Pair<Boolean, Int>>()

    /** [apply] (liked, likeCount) 로 화면 상태를 바꾼다 — 리듀서로 넘기면 된다. */
    fun toggle(
        scope: CoroutineScope,
        target: InteractionTarget,
        id: String,
        liked: Boolean,
        likeCount: Int,
        apply: (Boolean, Int) -> Unit,
    ) {
        // 진행 중인 요청이 없으면 지금 화면 값 = 서버 값
        if ((pending[id] ?: 0) == 0) confirmed[id] = liked to likeCount
        pending[id] = (pending[id] ?: 0) + 1
        val want = !liked
        val version = (versions[id] ?: 0) + 1
        versions[id] = version
        apply(want, (likeCount + if (want) 1 else -1).coerceAtLeast(0))

        scope.launch {
            val result = runCatching { mutex.withLock { setLike(target, id, want) } }
            result.onSuccess { confirmed[id] = it }
            val isLast = versions[id] == version
            if (isLast) {
                val (l, c) = result.getOrNull() ?: confirmed.getValue(id)
                apply(l, c)
            }
            val left = (pending[id] ?: 1) - 1
            if (left == 0) {
                pending.remove(id)
                confirmed.remove(id)
            } else {
                pending[id] = left
            }
        }
    }
}
