package com.gentlelady.reborn.domain.usecase

import com.gentlelady.reborn.domain.repository.TodoRepository
import com.gentlelady.reborn.model.OperationResult
import com.gentlelady.reborn.model.Todo
import com.gentlelady.reborn.model.UiError

class AddTodoUseCase(
    private val todoRepository: TodoRepository,
) {
    suspend operator fun invoke(title: String): OperationResult<List<Todo>> {
        if (title.isBlank()) {
            return OperationResult.Failure(UiError.Validation(message = "Todo title cannot be empty."))
        }
        return todoRepository.addTodo(title = title.trim())
    }
}
