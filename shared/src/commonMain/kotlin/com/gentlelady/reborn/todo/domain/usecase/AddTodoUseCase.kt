package com.gentlelady.reborn.todo.domain.usecase

import com.gentlelady.reborn.todo.domain.model.TodoDomainError
import com.gentlelady.reborn.todo.domain.model.TodoUseCaseResult
import com.gentlelady.reborn.todo.domain.repository.TodoRepository
import com.gentlelady.reborn.todo.domain.repository.TodoRepositoryResult

class AddTodoUseCase(
    private val repository: TodoRepository,
) {
    suspend operator fun invoke(title: String): TodoUseCaseResult {
        val trimmed = title.trim()
        if (trimmed.isBlank()) {
            return TodoUseCaseResult.Failure(
                error = TodoDomainError.Validation(message = "Todo title cannot be empty."),
            )
        }
        return repository.addTodo(title = trimmed).toUseCaseResult()
    }
}

private fun TodoRepositoryResult.toUseCaseResult(): TodoUseCaseResult {
    return when (this) {
        is TodoRepositoryResult.Success -> TodoUseCaseResult.Success(todos = todos)
        is TodoRepositoryResult.Failure -> TodoUseCaseResult.Failure(error = error)
    }
}
