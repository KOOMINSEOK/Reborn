package com.gentlelady.reborn.todo.data.repository

import com.gentlelady.reborn.todo.domain.model.Todo
import com.gentlelady.reborn.todo.domain.model.TodoDomainError
import com.gentlelady.reborn.todo.domain.repository.TodoRepository
import com.gentlelady.reborn.todo.domain.repository.TodoRepositoryResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TodoRepositoryImpl : TodoRepository {
    private val lock = Mutex()
    private val todos = mutableListOf<Todo>()
    private var nextId = 1L

    override suspend fun getTodos(): TodoRepositoryResult = lock.withLock {
        TodoRepositoryResult.Success(todos = todos.toList())
    }

    override suspend fun addTodo(title: String): TodoRepositoryResult = lock.withLock {
        todos += Todo(
            id = nextId.toString(),
            title = title,
            isCompleted = false,
        )
        nextId += 1
        TodoRepositoryResult.Success(todos = todos.toList())
    }

    override suspend fun toggleTodo(id: String): TodoRepositoryResult = lock.withLock {
        val index = todos.indexOfFirst { it.id == id }
        if (index == -1) {
            return TodoRepositoryResult.Failure(
                error = TodoDomainError.Unknown(message = "Todo not found."),
            )
        }
        val target = todos[index]
        todos[index] = target.copy(isCompleted = !target.isCompleted)
        TodoRepositoryResult.Success(todos = todos.toList())
    }

    override suspend fun deleteTodo(id: String): TodoRepositoryResult = lock.withLock {
        val removed = todos.removeAll { it.id == id }
        if (!removed) {
            return TodoRepositoryResult.Failure(
                error = TodoDomainError.Unknown(message = "Todo not found."),
            )
        }
        TodoRepositoryResult.Success(todos = todos.toList())
    }
}
