package com.gentlelady.reborn.feedwrite.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 사후 게시글 예약 설정(공개 시점/범위)과 대상 팔로워 선택을 화면 간 공유하는 뷰모델.
 * feed_write 네비게이션 그래프의 schedule/target_select 화면이 같은 인스턴스를 사용한다.
 */
class FeedWriteViewModel : ViewModel() {

    private val _state = MutableStateFlow(FeedWriteState())
    val state: StateFlow<FeedWriteState> = _state.asStateFlow()

    fun onIntent(intent: FeedWriteIntent) {
        _state.update { state ->
            when (intent) {
                is FeedWriteIntent.SelectScheduleOption -> state.copy(scheduleOption = intent.option)
                is FeedWriteIntent.SelectCustomDate -> state.copy(customDate = intent.date)
                is FeedWriteIntent.SelectVisibility -> state.copy(visibility = intent.visibility)
                is FeedWriteIntent.ToggleFollower -> {
                    val ids = state.selectedFollowerIds
                    val updated = if (intent.id in ids) ids - intent.id else ids + intent.id
                    state.copy(selectedFollowerIds = updated)
                }
            }
        }
    }
}
