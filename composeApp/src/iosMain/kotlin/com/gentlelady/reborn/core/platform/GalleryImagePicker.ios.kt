package com.gentlelady.reborn.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

// TODO: iOS 갤러리 피커 미구현 (프로젝트는 현재 Android 우선으로 진행 중)
@Composable
actual fun rememberGalleryImagePicker(onImagePicked: (ImageBitmap) -> Unit): () -> Unit {
    return {}
}
