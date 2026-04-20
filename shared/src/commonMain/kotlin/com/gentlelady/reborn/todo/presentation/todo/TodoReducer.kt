package com.gentlelady.reborn.todo.presentation.todo

import com.gentlelady.reborn.todo.domain.model.TodoDomainError
import com.gentlelady.reborn.todo.domain.model.TodoUseCaseResult

class TodoReducer {
    fun reduce(current: TodoState, result: TodoResult): TodoState {
        return when (result) {
            TodoResult.Loading -> current.copy(isLoading = true, error = null)
            is TodoResult.InputUpdated -> current.copy(inputText = result.text)
            is TodoResult.UseCaseResult -> current.reduceUseCaseResult(result)

            TodoResult.ClearError -> current.copy(error = null)
        }
    }
}

private fun TodoState.reduceUseCaseResult(result: TodoResult.UseCaseResult): TodoState {
    return when (val useCaseResult = result.value) {
        is TodoUseCaseResult.Success -> copy(
            isLoading = false,
            items = useCaseResult.todos,
            inputText = if (result.clearInput) "" else inputText,
            error = null,
        )

        is TodoUseCaseResult.Failure -> copy(
            isLoading = false,
            error = useCaseResult.error.toPresentationError(),
        )
    }
}

private fun TodoDomainError.toPresentationError(): TodoError {
    return when (this) {
        is TodoDomainError.Validation -> TodoError.Validation(message = message)
        is TodoDomainError.Unknown -> TodoError.Unknown(message = message)
    }
}
