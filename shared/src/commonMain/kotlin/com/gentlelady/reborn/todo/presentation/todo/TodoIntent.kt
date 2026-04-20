package com.gentlelady.reborn.todo.presentation.todo

sealed class TodoIntent {
    data object Load : TodoIntent()
    data class InputChanged(val text: String) : TodoIntent()
    data object AddClicked : TodoIntent()
    data class ToggleClicked(val id: String) : TodoIntent()
    data class DeleteClicked(val id: String) : TodoIntent()
    data object ErrorDismissed : TodoIntent()
}
