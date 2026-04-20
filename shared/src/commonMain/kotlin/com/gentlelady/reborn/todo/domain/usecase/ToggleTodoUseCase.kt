package com.gentlelady.reborn.todo.domain.usecase

import com.gentlelady.reborn.todo.domain.repository.TodoRepository
import com.gentlelady.reborn.todo.domain.repository.TodoRepositoryResult
import com.gentlelady.reborn.todo.domain.model.TodoUseCaseResult

class ToggleTodoUseCase(
    private val repository: TodoRepository,
) {
    suspend operator fun invoke(id: String): TodoUseCaseResult =
        repository.toggleTodo(id = id).toUseCaseResult()
}

private fun TodoRepositoryResult.toUseCaseResult(): TodoUseCaseResult {
    return when (this) {
        is TodoRepositoryResult.Success -> TodoUseCaseResult.Success(todos = todos)
        is TodoRepositoryResult.Failure -> TodoUseCaseResult.Failure(error = error)
    }
}
