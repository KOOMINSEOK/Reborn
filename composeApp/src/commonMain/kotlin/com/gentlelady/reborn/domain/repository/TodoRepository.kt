package com.gentlelady.reborn.domain.repository

import com.gentlelady.reborn.model.OperationResult
import com.gentlelady.reborn.model.Todo

interface TodoRepository {
    suspend fun getTodos(): OperationResult<List<Todo>>
    suspend fun addTodo(title: String): OperationResult<List<Todo>>
    suspend fun toggleTodo(id: String): OperationResult<List<Todo>>
    suspend fun deleteTodo(id: String): OperationResult<List<Todo>>
}
