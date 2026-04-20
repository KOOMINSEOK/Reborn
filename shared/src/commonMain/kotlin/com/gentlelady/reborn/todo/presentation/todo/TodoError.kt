package com.gentlelady.reborn.todo.presentation.todo

sealed class TodoError {
    data class Validation(val message: String) : TodoError()
    data class Unknown(val message: String) : TodoError()
}
