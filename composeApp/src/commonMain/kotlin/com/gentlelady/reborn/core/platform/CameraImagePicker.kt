package com.gentlelady.reborn.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * 카메라 앱을 직접 실행해 촬영한 사진을 받는 플랫폼별 런처.
 * [rememberGalleryImagePicker]와 달리 사진 보관함이 아닌 카메라 캡처 화면이 뜬다.
 */
@Composable
expect fun rememberCameraImagePicker(onImagePicked: (ImageBitmap) -> Unit): () -> Unit
