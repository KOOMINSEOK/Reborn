package com.gentlelady.reborn.userprofile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
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
    private val currentUserId: CurrentUserIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FollowListState())
    val state: StateFlow<FollowListState> = _state.asStateFlow()

    private val followSync = ToggleSync { id, follow -> setFollow(id, follow) }
    /** 목록 주인이 바뀔 때 (팔로우 결과도 버린다) */
    private var generation = 0
    /** 목록을 처음부터 다시 받을 때마다 — 그 전에 출발한 페이지 응답(더 불러오기 포함)을 버린다 */
    private var pageGeneration = 0

    fun onIntent(intent: FollowListIntent) {
        when (intent) {
            is FollowListIntent.Open -> {
                val s = _state.value
                if (s.userId == intent.userId) {
                    selectTab(intent.tab) // 같은 사람 — 다시 받지 않는다 (뒤로 돌아왔을 때 등)
                } else {
                    generation++
                    pageGeneration++
                    reduce(FollowListResult.Opened(intent.userId, intent.tab))
                    load(intent.tab, more = false)
                }
            }
            is FollowListIntent.SelectTab -> selectTab(intent.tab)
            is FollowListIntent.Retry -> load(_state.value.tab, more = false)
            is FollowListIntent.LoadMore -> load(_state.value.tab, more = true)
            is FollowListIntent.ToggleFollow -> toggleFollow(intent.userId)
            is FollowListIntent.MessageShown -> reduce(FollowListResult.Message(null))
            is FollowListIntent.Refresh -> refresh()
        }
    }

    /** 처음 열 때가 아니라 돌아왔을 때만: 지금 탭은 새로 받고, 다른 탭은 다음에 볼 때 받는다. */
    private fun refresh() {
        val s = _state.value
        if (s.userId.isEmpty() || !s.current.loaded) return
        FollowTab.entries.filter { it != s.tab }.forEach { reduce(FollowListResult.Invalidated(it)) }
        pageGeneration++
        reduce(FollowListResult.LoadMoreCancelled(s.tab))
        load(s.tab, more = false)
    }

    private fun selectTab(tab: FollowTab) {
        // 내 팔로잉 탭을 보면서 바꾼 팔로우는 떠날 때 무효화 (보는 중엔 줄을 남겨 되돌릴 수 있게)
        val s = _state.value
        if (s.tab == FollowTab.FOLLOWING && tab != FollowTab.FOLLOWING && s.userId == currentUserId()) {
            reduce(FollowListResult.Invalidated(FollowTab.FOLLOWING))
        }
        reduce(FollowListResult.TabSelected(tab))
        val page = _state.value.current
        if (!page.loaded && !page.isLoading) load(tab, more = false)
    }

    private fun load(tab: FollowTab, more: Boolean) {
        val s = _state.value
        val page = s.pages[tab]
        // 처음부터 다시 받는 건 더 불러오기보다 우선 (끝난 뒤 offset 0 으로 덮어쓴다)
        if (page?.isLoading == true || (more && page?.isLoadingMore == true)) return
        val offset = if (more) page?.nextOffset ?: return else 0
        val gen = pageGeneration
        reduce(FollowListResult.Loading(tab, more))
        viewModelScope.launch {
            val result = runCatching { getRelations(s.userId, tab, offset) }
            if (gen != pageGeneration) return@launch
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
            if (gen != generation) return@toggle
            reduce(FollowListResult.FollowChanged(userId, following))
            // 내 목록이면 "팔로잉" 탭의 사람 자체가 바뀐다 → 다음에 볼 때 다시 받는다
            // (지금 보고 있는 줄은 그대로 둬서 바로 되돌릴 수 있게)
            val s = _state.value
            if (s.userId == currentUserId() && s.tab != FollowTab.FOLLOWING) reduce(FollowListResult.Invalidated(FollowTab.FOLLOWING))
        }
    }

    private fun reduce(result: FollowListResult) = _state.update { FollowListReducer.reduce(it, result) }
}
