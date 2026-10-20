package com.gentlelady.reborn.feedwrite.presentation

object FeedWriteReducer {
    /** 화면 입력(동기) 인텐트. Submit 은 ViewModel 이 UseCase 결과로 [reduce] 한다. */
    fun reduce(state: FeedWriteState, intent: FeedWriteIntent): FeedWriteState = when (intent) {
        is FeedWriteIntent.SelectScheduleOption -> state.copy(scheduleOption = intent.option)
        is FeedWriteIntent.SelectCustomDate -> state.copy(customDate = intent.date)
        is FeedWriteIntent.SelectVisibility -> state.copy(visibility = intent.visibility)
        is FeedWriteIntent.ToggleFollower -> {
            val ids = state.selectedFollowerIds
            state.copy(selectedFollowerIds = if (intent.id in ids) ids - intent.id else ids + intent.id)
        }
        is FeedWriteIntent.ChangeCaption -> state.copy(caption = intent.caption, submitError = null)
        is FeedWriteIntent.AddImage ->
            if (state.images.size >= MAX_POST_IMAGES) state
            else state.copy(images = state.images + intent.jpeg, submitError = null)
        is FeedWriteIntent.RemoveImage -> state.copy(images = state.images.filterIndexed { i, _ -> i != intent.index })
        is FeedWriteIntent.Submit -> state
    }

    fun reduce(state: FeedWriteState, result: FeedWriteResult): FeedWriteState = when (result) {
        is FeedWriteResult.Submitting -> state.copy(isSubmitting = true, submitError = null)
        is FeedWriteResult.Submitted -> state.copy(isSubmitting = false, isSubmitted = true)
        is FeedWriteResult.SubmitFailed -> state.copy(isSubmitting = false, submitError = result.message)
    }
}
