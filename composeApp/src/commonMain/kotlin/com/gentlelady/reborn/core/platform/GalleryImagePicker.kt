package com.gentlelady.reborn.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * 갤러리(사진 선택기)를 여는 플랫폼별 런처를 반환한다.
 * 반환된 함수를 호출하면 시스템 사진 선택 UI가 뜨고, 사용자가 사진을 고르면 [onImagePicked]가 호출된다.
 */
@Composable
expect fun rememberGalleryImagePicker(onImagePicked: (ImageBitmap) -> Unit): () -> Unit
