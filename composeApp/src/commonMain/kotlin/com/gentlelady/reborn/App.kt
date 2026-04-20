package com.gentlelady.reborn

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gentlelady.reborn.presentation.todo.TodoState
import com.gentlelady.reborn.ui.todo.TodoScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            TodoScreen(
                state = TodoState(),
                onIntent = {},
            )
        }
    }
}
