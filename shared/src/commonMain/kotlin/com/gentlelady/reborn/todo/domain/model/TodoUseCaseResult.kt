package com.gentlelady.reborn.todo.domain.model

sealed class TodoUseCaseResult {
    data class Success(val todos: List<Todo>) : TodoUseCaseResult()
    data class Failure(val error: TodoDomainError) : TodoUseCaseResult()
}
