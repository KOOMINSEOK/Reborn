package com.gentlelady.reborn.todo.domain.repository

import com.gentlelady.reborn.todo.domain.model.Todo
import com.gentlelady.reborn.todo.domain.model.TodoDomainError

sealed class TodoRepositoryResult {
    data class Success(val todos: List<Todo>) : TodoRepositoryResult()
    data class Failure(val error: TodoDomainError) : TodoRepositoryResult()
}

interface TodoRepository {
    suspend fun getTodos(): TodoRepositoryResult
    suspend fun addTodo(title: String): TodoRepositoryResult
    suspend fun toggleTodo(id: String): TodoRepositoryResult
    suspend fun deleteTodo(id: String): TodoRepositoryResult
}
