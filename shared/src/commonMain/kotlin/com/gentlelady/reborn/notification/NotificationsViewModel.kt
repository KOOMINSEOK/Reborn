package com.gentlelady.reborn.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationsState(
    val items: List<Notification> = emptyList(),
    val nextCursor: String? = null,
    val isLoading: Boolean = true,
    val loadFailed: Boolean = false,
    val isLoadingMore: Boolean = false,
    /** 더 불러오기 실패 → 목록 끝에 "다시 시도" */
    val loadMoreFailed: Boolean = false,
)

sealed interface NotificationsIntent {
    /** 화면을 열 때 (돌아왔을 때도) — 처음부터 받고 받은 것까지 읽음 처리 */
    object Load : NotificationsIntent
    object LoadMore : NotificationsIntent
}

/**
 * 알림 목록. 받아 오면 그 중 가장 최신 알림까지 읽음 처리한다 (화면에선 이번에 새로 온 알림을 계속 강조).
 * 다시 받으면 이전 요청의 늦은 응답은 버린다 (generation).
 */
class NotificationsViewModel(
    private val getNotifications: GetNotificationsUseCase,
    private val markRead: MarkNotificationsReadUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    private var generation = 0
    private var loadJob: Job? = null

    fun onIntent(intent: NotificationsIntent) {
        when (intent) {
            is NotificationsIntent.Load -> load()
            is NotificationsIntent.LoadMore -> loadMore()
        }
    }

    private fun load() {
        val gen = ++generation
        loadJob?.cancel()
        // 다시 들어올 땐 이전 목록을 보여 주면서 받는다
        _state.update { it.copy(isLoading = it.items.isEmpty(), loadFailed = false, isLoadingMore = false, loadMoreFailed = false) }
        loadJob = viewModelScope.launch {
            val result = runCatching { getNotifications(null) }
            if (gen != generation) return@launch
            result
                .onSuccess { (items, next) ->
                    _state.update { it.copy(items = items, nextCursor = next, isLoading = false, loadFailed = false) }
                    // 본 것까지만 읽음 (보는 사이 새로 온 건 그대로 안 읽음)
                    items.firstOrNull { !it.read }?.let { runCatching { markRead(items.first().createdAt) } }
                }
                .onFailure { _state.update { it.copy(isLoading = false, loadFailed = it.items.isEmpty()) } }
        }
    }

    private fun loadMore() {
        val s = _state.value
        val cursor = s.nextCursor ?: return
        if (s.isLoading || s.isLoadingMore) return
        val gen = generation
        _state.update { it.copy(isLoadingMore = true, loadMoreFailed = false) }
        viewModelScope.launch {
            val result = runCatching { getNotifications(cursor) }
            if (gen != generation) return@launch
            result
                .onSuccess { (items, next) ->
                    _state.update { st ->
                        st.copy(
                            items = st.items + items.filter { new -> st.items.none { it.id == new.id } },
                            nextCursor = next,
                            isLoadingMore = false,
                        )
                    }
                }
                .onFailure { _state.update { it.copy(isLoadingMore = false, loadMoreFailed = true) } }
        }
    }
}
