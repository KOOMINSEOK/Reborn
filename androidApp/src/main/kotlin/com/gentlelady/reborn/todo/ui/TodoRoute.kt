package com.gentlelady.reborn.todo.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.gentlelady.reborn.todo.presentation.todo.TodoIntent
import com.gentlelady.reborn.todo.presentation.todo.TodoViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun TodoRoute() {
    val dependencies = remember { TodoRouteDependencies() }
    val viewModel = dependencies.viewModel
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(TodoIntent.Load)
    }

    TodoScreen(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

private class TodoRouteDependencies : KoinComponent {
    val viewModel: TodoViewModel by inject()
}
