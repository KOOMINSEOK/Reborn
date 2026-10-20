package com.gentlelady.reborn.core.platform

import androidx.compose.runtime.Composable

/**
 * 사진 보관함이 아닌 "파일" 앱(다운로드, 드라이브 등 문서 제공자)에서 이미지를 고르는 런처.
 */
// 업로드용 JPEG 바이트를 돌려준다. 미리보기는 Coil 이 바이트를 그대로 그린다.
@Composable
expect fun rememberFileImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit
