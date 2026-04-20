package com.gentlelady.reborn.presentation.todo

import com.gentlelady.reborn.model.Todo
import com.gentlelady.reborn.model.UiError

sealed class TodoResult {
    data object Loading : TodoResult()
    data class InputUpdated(val text: String) : TodoResult()
    data class TodosUpdated(
        val items: List<Todo>,
        val clearInput: Boolean = false,
    ) : TodoResult()
    data class Failure(val error: UiError) : TodoResult()
    data object ErrorDismissed : TodoResult()
}
