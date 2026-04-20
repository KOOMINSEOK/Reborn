package com.gentlelady.reborn.todo.presentation.todo

import com.gentlelady.reborn.todo.domain.model.TodoUseCaseResult

sealed class TodoResult {
    data object Loading : TodoResult()
    data class InputUpdated(val text: String) : TodoResult()
    data class UseCaseResult(
        val value: TodoUseCaseResult,
        val clearInput: Boolean = false,
    ) : TodoResult()
    data object ClearError : TodoResult()
}
