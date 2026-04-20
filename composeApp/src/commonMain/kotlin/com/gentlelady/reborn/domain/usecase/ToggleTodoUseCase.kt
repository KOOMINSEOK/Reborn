package com.gentlelady.reborn.domain.usecase

import com.gentlelady.reborn.domain.repository.TodoRepository
import com.gentlelady.reborn.model.OperationResult
import com.gentlelady.reborn.model.Todo

class ToggleTodoUseCase(
    private val todoRepository: TodoRepository,
) {
    suspend operator fun invoke(id: String): OperationResult<List<Todo>> = todoRepository.toggleTodo(id = id)
}
