package com.gentlelady.reborn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gentlelady.reborn.todo.di.initKoin
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
