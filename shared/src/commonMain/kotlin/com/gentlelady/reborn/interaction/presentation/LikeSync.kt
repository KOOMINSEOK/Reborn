package com.gentlelady.reborn.interaction.presentation

import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.interaction.domain.SetLikeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 켜고 끄는 버튼(좋아요·팔로우)을 화면 ViewModel 들이 같이 쓰는 도우미.
 * - 누르면 바로 반영(낙관), 서버 응답이 오면 서버 값으로.
 * - 요청은 순서대로 보낸다(Mutex 는 FIFO) → 빠르게 여러 번 눌러도 서버 최종 상태 = 마지막으로 누른 상태.
 * - 화면 반영은 그 대상의 마지막 탭이 끝날 때만 (중간 응답이 화면을 깜빡이게 하지 않게).
 * - 마지막 탭이 실패하면 "서버가 마지막으로 확인해 준 값"으로 되돌린다.
 *   (탭 직전 값이 아니라 — 그건 앞 탭의 낙관값일 수 있어 서버에 없던 상태다)
 *
 * [send] (id, 켤지) → 서버 기준 (켜짐, 개수).
 * ponytail: 요청 직렬화는 인스턴스 하나에 Mutex 하나 — 대상이 달라도 줄을 선다. 느려지면 대상별 Mutex.
 * 메인 스레드에서만 호출한다 (맵들은 동기화하지 않음).
 */
class ToggleSync(private val send: suspend (id: String, on: Boolean) -> Pair<Boolean, Int>) {
    private val mutex = Mutex()
    private val versions = mutableMapOf<String, Int>()
    private val pending = mutableMapOf<String, Int>()
    /** 대상별 서버가 확인한 (켜짐, 개수). 진행 중인 요청이 있을 때만 들고 있다. */
    private val confirmed = mutableMapOf<String, Pair<Boolean, Int>>()

    /** [apply] (켜짐, 개수) 로 화면 상태를 바꾼다 — 리듀서로 넘기면 된다. [onError] 는 마지막 탭이 실패했을 때. */
    fun toggle(
        scope: CoroutineScope,
        id: String,
        on: Boolean,
        count: Int,
        onError: (Throwable) -> Unit = {},
        apply: (Boolean, Int) -> Unit,
    ) {
        // 진행 중인 요청이 없으면 지금 화면 값 = 서버 값
        if ((pending[id] ?: 0) == 0) confirmed[id] = on to count
        pending[id] = (pending[id] ?: 0) + 1
        val want = !on
        val version = (versions[id] ?: 0) + 1
        versions[id] = version
        apply(want, (count + if (want) 1 else -1).coerceAtLeast(0))

        scope.launch {
            val result = runCatching { mutex.withLock { send(id, want) } }
            result.onSuccess { confirmed[id] = it }
            if (versions[id] == version) {
                val (l, c) = result.getOrNull() ?: confirmed.getValue(id)
                apply(l, c)
                result.exceptionOrNull()?.let(onError)
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

/** 좋아요 전용 — 대상 종류(글·추억 글)마다 [ToggleSync] 하나. */
class LikeSync(private val setLike: SetLikeUseCase) {
    private val syncs = mutableMapOf<InteractionTarget, ToggleSync>()

    fun toggle(
        scope: CoroutineScope,
        target: InteractionTarget,
        id: String,
        liked: Boolean,
        likeCount: Int,
        apply: (Boolean, Int) -> Unit,
    ) = syncs.getOrPut(target) { ToggleSync { i, want -> setLike(target, i, want) } }
        .toggle(scope, id, liked, likeCount, apply = apply)
}
