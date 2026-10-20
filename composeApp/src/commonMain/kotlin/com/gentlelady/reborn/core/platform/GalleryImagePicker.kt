package com.gentlelady.reborn.core.platform

import androidx.compose.runtime.Composable

/**
 * 갤러리(사진 선택기)를 여는 플랫폼별 런처를 반환한다.
 * 반환된 함수를 호출하면 시스템 사진 선택 UI가 뜨고, 사용자가 사진을 고르면 [onImagePicked]가 호출된다.
 */
// 업로드용 JPEG 바이트를 돌려준다. 미리보기는 Coil 이 바이트를 그대로 그린다.
@Composable
expect fun rememberGalleryImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit
