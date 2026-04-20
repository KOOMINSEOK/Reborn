package com.gentlelady.reborn

import androidx.compose.ui.window.ComposeUIViewController
import com.gentlelady.reborn.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}
