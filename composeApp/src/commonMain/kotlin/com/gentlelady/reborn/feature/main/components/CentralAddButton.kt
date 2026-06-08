package com.gentlelady.reborn.feature.main.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.gentlelady.reborn.core.theme.RebornDeepBlue

@Composable
fun RowScope.CentralAddButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
    ) {
        FloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
            containerColor = RebornDeepBlue,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(4.dp, 8.dp),
            modifier = Modifier
                .size(54.dp)
                .border(width = 4.dp, color = Color.White, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview
@Composable
fun CentralAddButtonPreview() {
    MaterialTheme {
        Surface {
            NavigationBar(modifier = Modifier.height(84.dp)) {
                CentralAddButton(onClick = {})
            }
        }
    }
}