package com.gentlelady.reborn.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

// TODO: iOS 파일 피커 미구현 (프로젝트는 현재 Android 우선으로 진행 중)
@Composable
actual fun rememberFileImagePicker(onImagePicked: (ImageBitmap) -> Unit): () -> Unit {
    return {}
}
