package com.gentlelady.reborn.core.platform

import androidx.compose.runtime.Composable

// TODO: 웹(Wasm) 갤러리 피커 미구현 (프로젝트는 현재 Android 우선으로 진행 중)
@Composable
actual fun rememberGalleryImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit {
    return {}
}
