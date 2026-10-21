package com.gentlelady.reborn.userprofile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.interaction.presentation.ToggleSync
import com.gentlelady.reborn.userprofile.domain.FollowTab
import com.gentlelady.reborn.userprofile.domain.GetRelationsUseCase
import com.gentlelady.reborn.userprofile.domain.SetFollowUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 팔로워·팔로잉 목록 화면. 탭은 처음 열 때 한 번 받고, 스크롤로 이어 받는다.
 * 다른 사람으로 [FollowListIntent.Open] 하면 처음부터 — 늦은 응답은 버린다 (generation).
 */
class FollowListViewModel(
    private val getRelations: GetRelationsUseCase,
    setFollow: SetFollowUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FollowListState())
    val state: StateFlow<FollowListState> = _state.asStateFlow()

    private val followSync = ToggleSync { id, follow -> setFollow(id, follow) }
    private var generation = 0

    fun onIntent(intent: FollowListIntent) {
        when (intent) {
            is FollowListIntent.Open -> {
                val s = _state.value
                if (s.userId == intent.userId) {
                    selectTab(intent.tab) // 같은 사람 — 다시 받지 않는다 (뒤로 돌아왔을 때 등)
                } else {
                    generation++
                    reduce(FollowListResult.Opened(intent.userId, intent.tab))
                    load(intent.tab, more = false)
                }
            }
            is FollowListIntent.SelectTab -> selectTab(intent.tab)
            is FollowListIntent.Retry -> load(_state.value.tab, more = false)
            is FollowListIntent.LoadMore -> load(_state.value.tab, more = true)
            is FollowListIntent.ToggleFollow -> toggleFollow(intent.userId)
            is FollowListIntent.MessageShown -> reduce(FollowListResult.Message(null))
        }
    }

    private fun selectTab(tab: FollowTab) {
        reduce(FollowListResult.TabSelected(tab))
        val page = _state.value.current
        if (!page.loaded && !page.isLoading) load(tab, more = false)
    }

    private fun load(tab: FollowTab, more: Boolean) {
        val s = _state.value
        val page = s.pages[tab]
        if (page?.isLoading == true || page?.isLoadingMore == true) return
        val offset = if (more) page?.nextOffset ?: return else 0
        val gen = generation
        reduce(FollowListResult.Loading(tab, more))
        viewModelScope.launch {
            val result = runCatching { getRelations(s.userId, tab, offset) }
            if (gen != generation) return@launch
            result
                .onSuccess { (users, next) -> reduce(FollowListResult.Loaded(tab, users, next, append = more)) }
                .onFailure { reduce(FollowListResult.LoadFailed(tab, more)) }
        }
    }

    private fun toggleFollow(userId: String) {
        val user = _state.value.pages.values.flatMap { it.users }.firstOrNull { it.id == userId } ?: return
        if (user.isMe) return
        val gen = generation
        // 목록엔 팔로워 수가 없다 → 개수는 0 으로 두고 켜짐 여부만 쓴다
        followSync.toggle(
            viewModelScope, userId, user.followedByMe, 0,
            onError = { if (gen == generation) reduce(FollowListResult.Message(followErrorMessage(it))) },
        ) { following, _ ->
            if (gen == generation) reduce(FollowListResult.FollowChanged(userId, following))
        }
    }

    private fun reduce(result: FollowListResult) = _state.update { FollowListReducer.reduce(it, result) }
}
