package com.gentlelady.reborn.di

import com.gentlelady.reborn.data.repository.TodoRepositoryImpl
import com.gentlelady.reborn.domain.repository.TodoRepository
import com.gentlelady.reborn.domain.usecase.AddTodoUseCase
import com.gentlelady.reborn.domain.usecase.DeleteTodoUseCase
import com.gentlelady.reborn.domain.usecase.GetTodosUseCase
import com.gentlelady.reborn.domain.usecase.ToggleTodoUseCase
import com.gentlelady.reborn.presentation.todo.TodoReducer
import com.gentlelady.reborn.presentation.todo.TodoViewModel
import org.koin.dsl.module

val todoModule = module {
    single<TodoRepository> { TodoRepositoryImpl() }

    factory { GetTodosUseCase(todoRepository = get()) }
    factory { AddTodoUseCase(todoRepository = get()) }
    factory { ToggleTodoUseCase(todoRepository = get()) }
    factory { DeleteTodoUseCase(todoRepository = get()) }

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
