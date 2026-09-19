package com.gentlelady.reborn.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * 사진 보관함이 아닌 "파일" 앱(다운로드, 드라이브 등 문서 제공자)에서 이미지를 고르는 런처.
 */
@Composable
expect fun rememberFileImagePicker(onImagePicked: (ImageBitmap) -> Unit): () -> Unit
