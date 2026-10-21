package com.gentlelady.reborn.userprofile.presentation

import com.gentlelady.reborn.userprofile.domain.FollowTab
import com.gentlelady.reborn.userprofile.domain.FollowUser

/** 탭 하나(팔로워 또는 팔로잉)의 목록 */
data class RelationPage(
    val users: List<FollowUser> = emptyList(),
    /** 다음 페이지 offset. null 이면 끝 */
    val nextOffset: Int? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val loadFailed: Boolean = false,
    /** 한 번이라도 받았나 (탭을 처음 열 때만 받는다) */
    val loaded: Boolean = false,
)

data class FollowListState(
    val userId: String = "",
    val tab: FollowTab = FollowTab.FOLLOWERS,
    val pages: Map<FollowTab, RelationPage> = emptyMap(),
    /** 한 번 보여주고 지우는 안내 (팔로우 실패 등) */
    val message: String? = null,
) {
    val current: RelationPage get() = pages[tab] ?: RelationPage()
}

sealed interface FollowListIntent {
    data class Open(val userId: String, val tab: FollowTab) : FollowListIntent
    data class SelectTab(val tab: FollowTab) : FollowListIntent
    object Retry : FollowListIntent
    object LoadMore : FollowListIntent
    data class ToggleFollow(val userId: String) : FollowListIntent
    object MessageShown : FollowListIntent
}

sealed interface FollowListResult {
    data class Opened(val userId: String, val tab: FollowTab) : FollowListResult
    data class TabSelected(val tab: FollowTab) : FollowListResult
    data class Loading(val tab: FollowTab, val more: Boolean) : FollowListResult
    data class Loaded(val tab: FollowTab, val users: List<FollowUser>, val nextOffset: Int?, val append: Boolean) : FollowListResult
    data class LoadFailed(val tab: FollowTab, val more: Boolean) : FollowListResult
    /** 같은 사람이 두 탭에 다 있을 수 있다 → 둘 다 고친다 */
    data class FollowChanged(val userId: String, val following: Boolean) : FollowListResult
    data class Message(val text: String?) : FollowListResult
}

object FollowListReducer {
    fun reduce(state: FollowListState, result: FollowListResult): FollowListState = when (result) {
        is FollowListResult.Opened -> FollowListState(userId = result.userId, tab = result.tab)
        is FollowListResult.TabSelected -> state.copy(tab = result.tab)
        is FollowListResult.Loading -> state.updatePage(result.tab) {
            if (result.more) it.copy(isLoadingMore = true) else it.copy(isLoading = true, loadFailed = false)
        }
        is FollowListResult.Loaded -> state.updatePage(result.tab) {
            it.copy(
                users = if (result.append) it.users + result.users.filter { new -> it.users.none { old -> old.id == new.id } }
                else result.users,
                nextOffset = result.nextOffset,
                isLoading = false,
                isLoadingMore = false,
                loadFailed = false,
                loaded = true,
            )
        }
        is FollowListResult.LoadFailed -> state.updatePage(result.tab) {
            if (result.more) it.copy(isLoadingMore = false) else it.copy(isLoading = false, loadFailed = true)
        }
        is FollowListResult.FollowChanged -> state.copy(
            pages = state.pages.mapValues { (_, page) ->
                page.copy(users = page.users.map { if (it.id == result.userId) it.copy(followedByMe = result.following) else it })
            },
        )
        is FollowListResult.Message -> state.copy(message = result.text)
    }

    private fun FollowListState.updatePage(tab: FollowTab, change: (RelationPage) -> RelationPage) =
        copy(pages = pages + (tab to change(pages[tab] ?: RelationPage())))
}
