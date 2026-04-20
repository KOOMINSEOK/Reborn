package com.gentlelady.reborn.todo.presentation.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.todo.domain.model.TodoUseCaseResult
import com.gentlelady.reborn.todo.domain.usecase.AddTodoUseCase
import com.gentlelady.reborn.todo.domain.usecase.DeleteTodoUseCase
import com.gentlelady.reborn.todo.domain.usecase.GetTodosUseCase
import com.gentlelady.reborn.todo.domain.usecase.ToggleTodoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel(
    private val getTodosUseCase: GetTodosUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val toggleTodoUseCase: ToggleTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val reducer: TodoReducer,
) : ViewModel() {

    private val _state = MutableStateFlow(TodoState())
    val state: StateFlow<TodoState> = _state.asStateFlow()

    fun handleIntent(intent: TodoIntent) {
        when (intent) {
            TodoIntent.Load -> execute(clearInput = false) { getTodosUseCase() }
            is TodoIntent.InputChanged -> emit(TodoResult.InputUpdated(text = intent.text))
            TodoIntent.AddClicked -> execute(clearInput = true) {
                addTodoUseCase(title = _state.value.inputText)
            }

            is TodoIntent.ToggleClicked -> execute(clearInput = false) {
                toggleTodoUseCase(id = intent.id)
            }

            is TodoIntent.DeleteClicked -> execute(clearInput = false) {
                deleteTodoUseCase(id = intent.id)
            }

            TodoIntent.ErrorDismissed -> emit(TodoResult.ClearError)
        }
    }

    private fun execute(
        clearInput: Boolean,
        block: suspend () -> TodoUseCaseResult,
    ) {
        viewModelScope.launch {
            emit(TodoResult.Loading)
            emit(TodoResult.UseCaseResult(value = block(), clearInput = clearInput))
        }
    }

    private fun emit(result: TodoResult) {
        _state.value = reducer.reduce(current = _state.value, result = result)
    }
}
