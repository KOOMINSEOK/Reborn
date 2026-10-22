package com.gentlelady.reborn.message.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.data.MockDataSource
import com.gentlelady.reborn.message.data.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SEARCH_DEBOUNCE_MS = 300L

data class UserSearchState(
    val query: String = "",
    /** 검색어가 없으면 추천(내가 팔로우하는 사람), 있으면 검색 결과 */
    val users: List<SuggestedUser> = emptyList(),
    val isSearching: Boolean = false,
)

/** 메시지 보낼 사람 찾기. 화면(내비게이션 항목)마다 하나. */
class UserSearchViewModel(
    private val repository: ChatRepository,
    private val currentUserId: CurrentUserIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(UserSearchState())
    val state: StateFlow<UserSearchState> = _state.asStateFlow()
    private var job: Job? = null

    init {
        search("")
    }

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
        search(query.trim())
    }

    /** 입력이 멈추면 찾는다. 늦게 온 옛 결과가 덮지 않게 이전 요청은 취소 */
    private fun search(q: String) {
        job?.cancel()
        job = viewModelScope.launch {
            val me = currentUserId()
            if (me == null) {
                // 둘러보기 — 목업
                _state.update { it.copy(users = MockDataSource.messageSuggestedUsers.filter { u -> q.isEmpty() || q in u.name || q in u.handle }) }
                return@launch
            }
            if (q.isNotEmpty()) delay(SEARCH_DEBOUNCE_MS)
            _state.update { it.copy(isSearching = true) }
            val users = runCatching { if (q.isEmpty()) repository.following(me) else repository.search(q) }.getOrNull()
            _state.update { it.copy(users = users ?: it.users, isSearching = false) }
        }
    }
}
