package com.gentlelady.reborn.presentation.todo

import com.gentlelady.reborn.model.Todo
import com.gentlelady.reborn.model.UiError

data class TodoState(
    val isLoading: Boolean = false,
    val items: List<Todo> = emptyList(),
    val inputText: String = "",
    val error: UiError? = null,
)
