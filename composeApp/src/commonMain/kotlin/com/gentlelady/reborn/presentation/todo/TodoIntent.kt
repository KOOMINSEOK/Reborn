package com.gentlelady.reborn.presentation.todo

sealed class TodoIntent {
    data object LoadTodos : TodoIntent()
    data class InputChanged(val text: String) : TodoIntent()
    data object AddTodoClicked : TodoIntent()
    data class ToggleTodoClicked(val id: String) : TodoIntent()
    data class DeleteTodoClicked(val id: String) : TodoIntent()
    data object DismissError : TodoIntent()
}
