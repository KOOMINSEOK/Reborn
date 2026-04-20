package com.gentlelady.reborn.presentation.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.domain.usecase.AddTodoUseCase
import com.gentlelady.reborn.domain.usecase.DeleteTodoUseCase
import com.gentlelady.reborn.domain.usecase.GetTodosUseCase
import com.gentlelady.reborn.domain.usecase.ToggleTodoUseCase
import com.gentlelady.reborn.model.OperationResult
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
            TodoIntent.LoadTodos -> runLoadingAction(clearInput = false) { getTodosUseCase() }
            is TodoIntent.InputChanged -> emit(TodoResult.InputUpdated(intent.text))
            TodoIntent.AddTodoClicked -> runLoadingAction(clearInput = true) {
                addTodoUseCase(title = state.value.inputText)
            }

            is TodoIntent.ToggleTodoClicked -> runLoadingAction(clearInput = false) {
                toggleTodoUseCase(id = intent.id)
            }

            is TodoIntent.DeleteTodoClicked -> runLoadingAction(clearInput = false) {
                deleteTodoUseCase(id = intent.id)
            }

            TodoIntent.DismissError -> emit(TodoResult.ErrorDismissed)
        }
    }

    private fun runLoadingAction(
        clearInput: Boolean,
        action: suspend () -> OperationResult<List<com.gentlelady.reborn.model.Todo>>,
    ) {
        viewModelScope.launch {
            emit(TodoResult.Loading)
            when (val result = action()) {
                is OperationResult.Success -> {
                    emit(TodoResult.TodosUpdated(items = result.data, clearInput = clearInput))
                }

                is OperationResult.Failure -> emit(TodoResult.Failure(result.error))
            }
        }
    }

    private fun emit(result: TodoResult) {
        _state.value = reducer.reduce(_state.value, result)
    }
}
