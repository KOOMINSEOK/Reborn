package com.gentlelady.reborn.todo.presentation.todo

import com.gentlelady.reborn.todo.domain.model.Todo

data class TodoState(
    val isLoading: Boolean = false,
    val items: List<Todo> = emptyList(),
    val inputText: String = "",
    val error: TodoError? = null,
)
