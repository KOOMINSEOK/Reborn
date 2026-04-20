package com.gentlelady.reborn.domain.usecase

import com.gentlelady.reborn.domain.repository.TodoRepository
import com.gentlelady.reborn.model.OperationResult
import com.gentlelady.reborn.model.Todo

class GetTodosUseCase(
    private val todoRepository: TodoRepository,
) {
    suspend operator fun invoke(): OperationResult<List<Todo>> = todoRepository.getTodos()
}
