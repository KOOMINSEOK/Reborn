package com.gentlelady.reborn.onboarding.presentation

import androidx.lifecycle.ViewModel
import com.gentlelady.reborn.data.OnboardingMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OnboardingViewModel : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState(pages = OnboardingMockData.pages))
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun handleIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.PageChanged -> _state.update {
                it.copy(currentPage = intent.page)
            }

            OnboardingIntent.NextClicked -> _state.update {
                val isLastPage = it.currentPage == it.pages.lastIndex
                if (isLastPage) {
                    it.copy(isCompleted = true)
                } else {
                    it.copy(currentPage = it.currentPage + 1)
                }
            }
        }
    }
}
