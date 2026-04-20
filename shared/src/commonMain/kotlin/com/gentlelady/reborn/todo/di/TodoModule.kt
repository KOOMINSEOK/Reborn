package com.gentlelady.reborn.todo.di

import com.gentlelady.reborn.todo.data.repository.TodoRepositoryImpl
import com.gentlelady.reborn.todo.domain.repository.TodoRepository
import com.gentlelady.reborn.todo.domain.usecase.AddTodoUseCase
import com.gentlelady.reborn.todo.domain.usecase.DeleteTodoUseCase
import com.gentlelady.reborn.todo.domain.usecase.GetTodosUseCase
import com.gentlelady.reborn.todo.domain.usecase.ToggleTodoUseCase
import com.gentlelady.reborn.todo.presentation.todo.TodoReducer
import com.gentlelady.reborn.todo.presentation.todo.TodoViewModel
import org.koin.dsl.module

val todoModule = module {
    single<TodoRepository> { TodoRepositoryImpl() }

    factory { GetTodosUseCase(repository = get()) }
    factory { AddTodoUseCase(repository = get()) }
    factory { ToggleTodoUseCase(repository = get()) }
    factory { DeleteTodoUseCase(repository = get()) }

    factory { TodoReducer() }
    factory {
        TodoViewModel(
            getTodosUseCase = get(),
            addTodoUseCase = get(),
            toggleTodoUseCase = get(),
            deleteTodoUseCase = get(),
            reducer = get(),
        )
    }
}
