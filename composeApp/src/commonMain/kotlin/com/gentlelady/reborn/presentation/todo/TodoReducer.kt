package com.gentlelady.reborn.presentation.todo

class TodoReducer {
    fun reduce(currentState: TodoState, result: TodoResult): TodoState {
        return when (result) {
            TodoResult.Loading -> currentState.copy(
                isLoading = true,
                error = null,
            )

            is TodoResult.InputUpdated -> currentState.copy(
                inputText = result.text,
            )

            is TodoResult.TodosUpdated -> currentState.copy(
                isLoading = false,
                items = result.items,
                inputText = if (result.clearInput) "" else currentState.inputText,
                error = null,
            )

            is TodoResult.Failure -> currentState.copy(
                isLoading = false,
                error = result.error,
            )

            TodoResult.ErrorDismissed -> currentState.copy(
                error = null,
            )
        }
    }
}
