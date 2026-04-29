package com.gentlelady.reborn.feature.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.todo.presentation.todo.TodoError
import com.gentlelady.reborn.todo.presentation.todo.TodoIntent
import com.gentlelady.reborn.todo.presentation.todo.TodoState

@Composable
fun TodoScreen(
    state: TodoState,
    onIntent: (TodoIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Todo",
            style = MaterialTheme.typography.headlineMedium,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = state.inputText,
                onValueChange = { onIntent(TodoIntent.InputChanged(it)) },
                enabled = !state.isLoading,
                label = { Text("New todo") },
                singleLine = true,
            )
            Button(
                onClick = { onIntent(TodoIntent.AddClicked) },
                enabled = !state.isLoading,
            ) {
                Text("Add")
            }
        }

        state.error?.let { error ->
            ErrorBanner(
                error = error,
                onDismiss = { onIntent(TodoIntent.ErrorDismissed) },
            )
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = state.items,
                key = { it.id },
            ) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = item.isCompleted,
                            onCheckedChange = { onIntent(TodoIntent.ToggleClicked(item.id)) },
                            enabled = !state.isLoading,
                        )
                        Text(
                            modifier = Modifier.weight(1f),
                            text = item.title,
                            textDecoration = if (item.isCompleted) {
                                TextDecoration.LineThrough
                            } else {
                                TextDecoration.None
                            },
                        )
                        Button(
                            onClick = { onIntent(TodoIntent.DeleteClicked(item.id)) },
                            enabled = !state.isLoading,
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorBanner(
    error: TodoError,
    onDismiss: () -> Unit,
) {
    val message = when (error) {
        is TodoError.Validation -> error.message
        is TodoError.Unknown -> error.message
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = message,
                color = MaterialTheme.colorScheme.error,
            )
            Button(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    }
}
