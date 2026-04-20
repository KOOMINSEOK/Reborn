package com.gentlelady.reborn.data.repository

import com.gentlelady.reborn.domain.repository.TodoRepository
import com.gentlelady.reborn.model.OperationResult
import com.gentlelady.reborn.model.Todo
import com.gentlelady.reborn.model.UiError
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TodoRepositoryImpl : TodoRepository {

    private val lock = Mutex()
    private val todos = mutableListOf<Todo>()
    private var nextId = 1L

    override suspend fun getTodos(): OperationResult<List<Todo>> = lock.withLock {
        OperationResult.Success(todos.toList())
    }

    override suspend fun addTodo(title: String): OperationResult<List<Todo>> = lock.withLock {
        val todo = Todo(
            id = nextId.toString(),
            title = title,
            isCompleted = false,
        )
        nextId += 1
        todos.add(todo)
        OperationResult.Success(todos.toList())
    }

    override suspend fun toggleTodo(id: String): OperationResult<List<Todo>> = lock.withLock {
        val index = todos.indexOfFirst { it.id == id }
        if (index == -1) {
            return OperationResult.Failure(
                UiError.Unknown(message = "Todo not found."),
            )
        }
        val target = todos[index]
        todos[index] = target.copy(isCompleted = !target.isCompleted)
        OperationResult.Success(todos.toList())
    }

    override suspend fun deleteTodo(id: String): OperationResult<List<Todo>> = lock.withLock {
        val removed = todos.removeAll { it.id == id }
        if (!removed) {
            return OperationResult.Failure(
                UiError.Unknown(message = "Todo not found."),
            )
        }
        OperationResult.Success(todos.toList())
    }
}
