package com.gentlelady.reborn.feedwrite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.feedwrite.domain.usecase.CreatePostUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone

/**
 * 게시글 작성 플로우(본문·사진 → 예약 설정 → 대상 선택) 전체가 공유하는 뷰모델.
 * feed_write 네비게이션 그래프의 모든 화면이 같은 인스턴스를 사용한다.
 */
class FeedWriteViewModel(
    private val createPostUseCase: CreatePostUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FeedWriteState())
    val state: StateFlow<FeedWriteState> = _state.asStateFlow()

    fun onIntent(intent: FeedWriteIntent) {
        if (intent is FeedWriteIntent.Submit) {
            submit(intent.type)
        } else {
            _state.update { FeedWriteReducer.reduce(it, intent) }
        }
    }

    private fun submit(type: FeedPostType) {
        val current = _state.value
        if (current.isSubmitting || current.isSubmitted) return
        viewModelScope.launch {
            _state.update { FeedWriteReducer.reduce(it, FeedWriteResult.Submitting) }
            val result = createPostUseCase(
                caption = current.caption,
                images = current.images,
                isPosthumous = type == FeedPostType.POSTHUMOUS,
                publishAt = current.publishAt(type, Clock.System.now(), TimeZone.currentSystemDefault()),
            )
            _state.update { FeedWriteReducer.reduce(it, result) }
        }
    }
}
