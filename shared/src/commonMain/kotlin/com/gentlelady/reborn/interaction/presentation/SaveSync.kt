package com.gentlelady.reborn.interaction.presentation

import com.gentlelady.reborn.core.network.InteractionTarget
import com.gentlelady.reborn.core.network.RebornApi
import kotlinx.coroutines.CoroutineScope

/** 저장 버튼 — 좋아요와 같은 방식(바로 반영·순서대로 보냄·실패하면 서버 값으로). 개수는 없다. */
class SaveSync(private val api: RebornApi) {
    private val syncs = mutableMapOf<InteractionTarget, ToggleSync>()

    fun toggle(
        scope: CoroutineScope,
        target: InteractionTarget,
        id: String,
        saved: Boolean,
        onError: (Throwable) -> Unit = {},
        apply: (Boolean) -> Unit,
    ) = syncs.getOrPut(target) { ToggleSync { i, want -> api.setSaved(target, i, want); want to 0 } }
        .toggle(scope, id, saved, 0, onError) { on, _ -> apply(on) }
}
