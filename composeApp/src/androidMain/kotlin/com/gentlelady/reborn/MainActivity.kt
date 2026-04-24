package com.gentlelady.reborn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gentlelady.reborn.di.initKoin
import com.gentlelady.reborn.todo.ui.TodoRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initKoin()

        setContent {
            TodoRoute()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    TodoRoute()
}
